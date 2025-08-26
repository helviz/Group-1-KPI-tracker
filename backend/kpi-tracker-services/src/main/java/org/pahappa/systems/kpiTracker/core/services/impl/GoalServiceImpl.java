package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.dao.GoalDao;
import org.pahappa.systems.kpiTracker.core.services.GoalService;
import org.pahappa.systems.kpiTracker.core.services.ProgressCalculationService;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.models.goalMgt.Goal;
import org.pahappa.systems.kpiTracker.models.kpi.KPI;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;

@Service("goalService")
@Transactional
public class GoalServiceImpl extends GenericServiceImpl<Goal> implements GoalService {

    @Autowired
    private GoalDao goalDao;

    @Autowired
    private ProgressCalculationService progressCalculationService;

    /**
     * CORE MBO LOGIC
     * Overrides the default save behavior to trigger the MBO progress rollup.
     */
    @Override
    public Goal saveInstance(Goal goal) throws ValidationFailedException, OperationFailedException {
        // Calculate progress for Individual goals based on KPIs
        if (goal.isIndividualGoal()) {
            calculateIndividualGoalProgress(goal);
        }

        // Save the goal
        Goal savedGoal = super.save(goal);

        // Trigger the chain reaction to update parent progress
        propagateProgressUpwards(savedGoal);

        return savedGoal;
    }

    /**
     * Calculate progress for Individual goals based on their KPIs
     */
    private void calculateIndividualGoalProgress(Goal goal) {
        if (goal.getKpis() == null || goal.getKpis().isEmpty()) {
            goal.setProgress(0.0);
            return;
        }

        BigDecimal totalProgress = BigDecimal.ZERO;
        int kpiCount = goal.getKpis().size();

        for (KPI kpi : goal.getKpis()) {
            totalProgress = totalProgress.add(kpi.getProgressPercentage());
        }

        // Calculate average progress
        double averageProgress = totalProgress
                .divide(BigDecimal.valueOf(kpiCount), 2, RoundingMode.HALF_UP)
                .doubleValue();

        goal.setProgress(averageProgress);
    }

    /**
     * The recursive engine for the MBO system.
     */
    private void propagateProgressUpwards(Goal goal) throws ValidationFailedException, OperationFailedException {
        Goal parentGoal = goal.getParentGoal();

        if (parentGoal == null) {
            return;
        }

        Set<Goal> childGoals = parentGoal.getChildGoals();
        double newParentProgress = progressCalculationService.calculateWeightedAverage(childGoals);
        parentGoal.setProgress(newParentProgress);

        // Use super.save() to save the updated parent. This will continue the
        // recursion.
        super.save(parentGoal);
    }

    /**
     * Get goals filtered by user context (My Goals, My Team, My Department,
     * Organization)
     */
    @Override
    public List<Goal> getGoalsByUserContext(String context, String userId, int offset, int limit) {
        return goalDao.getGoalsByUserContext(context, userId, offset, limit);
    }

    /**
     * Count goals filtered by user context
     */
    @Override
    public int countGoalsByUserContext(String context, String userId) {
        return goalDao.countGoalsByUserContext(context, userId);
    }

    /**
     * Get available parent goals for a given goal level
     */
    @Override
    public List<Goal> getAvailableParentGoals(String goalLevelName, String userId) {
        return goalDao.getAvailableParentGoals(goalLevelName, userId);
    }

    // --- CUSTOM QUERY METHODS ---
    public List<Goal> getGoalsForDepartment(Department department, int offset, int limit) {
        return goalDao.getGoalsForDepartment(department, offset, limit);
    }

    public int countGoalsForDepartment(Department department) {
        return goalDao.countGoalsForDepartment(department);
    }

    // --- REQUIRED OVERRIDE from GenericServiceImpl ---
    @Override
    public boolean isDeletable(Goal instance) throws OperationFailedException {
        if (instance == null) {
            return false;
        }

        // Check if the goal has child goals
        if (instance.getChildGoals() != null && !instance.getChildGoals().isEmpty()) {
            return false;
        }

        // Check if the goal has KPIs (for Individual goals)
        if (instance.getKpis() != null && !instance.getKpis().isEmpty()) {
            return false;
        }

        return true;
    }
}