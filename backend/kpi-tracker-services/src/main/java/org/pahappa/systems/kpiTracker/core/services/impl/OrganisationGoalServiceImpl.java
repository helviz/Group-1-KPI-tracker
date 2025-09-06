package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.dao.OrganisationGoalDao;
import org.pahappa.systems.kpiTracker.core.services.OrganisationGoalService;
import org.pahappa.systems.kpiTracker.models.goalMgt.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.OrganisationGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.GoalPeriod;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Service("organisationGoalService")
@Transactional
public class OrganisationGoalServiceImpl extends GenericServiceImpl<OrganisationGoal>
        implements OrganisationGoalService {

    @Autowired
    private OrganisationGoalDao organisationGoalDao;

    @Override
    public OrganisationGoal saveInstance(OrganisationGoal goal)
            throws ValidationFailedException, OperationFailedException {
        Validate.notNull(goal, "Organisation goal cannot be null");
        Validate.hasText(goal.getTitle(), "Goal title is required");
        Validate.notNull(goal.getGoalPeriod(), "Goal period is required");
        Validate.notNull(goal.getEndDate(), "End date is required");
        Validate.notNull(goal.getOwner(), "Goal owner is required");

        // Validate end date is in the future
        if (goal.getEndDate().before(new Date())) {
            throw new ValidationFailedException("End date must be in the future");
        }

        // Set default values
        if (goal.getProgress() == null) {
            goal.setProgress(BigDecimal.ZERO);
        }
        if (goal.getEvaluationTarget() == null) {
            goal.setEvaluationTarget(new BigDecimal("100.0"));
        }
        if (goal.getContributionToParent() == null) {
            goal.setContributionToParent(new BigDecimal("100.0"));
        }

        return super.save(goal);
    }

    @Override
    public OrganisationGoal createOrganisationGoal(OrganisationGoal goal)
            throws ValidationFailedException, OperationFailedException {
        return saveInstance(goal);
    }

    @Override
    public OrganisationGoal updateProgress(String goalId, BigDecimal newProgress)
            throws ValidationFailedException, OperationFailedException {
        Validate.hasText(goalId, "Goal ID is required");
        Validate.notNull(newProgress, "New progress value is required");

        OrganisationGoal goal = getInstanceByID(goalId);
        if (goal == null) {
            throw new ValidationFailedException("Organisation goal not found");
        }

        goal.updateProgress(newProgress);
        return super.save(goal);
    }

    @Override
    public BigDecimal calculateRollupProgress(OrganisationGoal goal) {
        if (goal == null) {
            return BigDecimal.ZERO;
        }
        return goal.calculateRollupProgress();
    }

    @Override
    public boolean validateChildGoalsContribution(OrganisationGoal goal) {
        if (goal == null) {
            return false;
        }
        return goal.validateDepartmentGoalsContribution();
    }

    @Override
    public boolean isDeletable(OrganisationGoal instance) throws OperationFailedException {
        // Check if goal has child department goals
        if (instance.getDepartmentGoals() != null && !instance.getDepartmentGoals().isEmpty()) {
            return false; // Cannot delete if it has child goals
        }
        return true;
    }

    @Override
    public List<OrganisationGoal> findAllActive() {
        return organisationGoalDao.findAllActive();
    }

    @Override
    public List<OrganisationGoal> findByGoalPeriod(GoalPeriod goalPeriod) throws ValidationFailedException {
        Validate.notNull(goalPeriod, "Goal period cannot be null");
        return organisationGoalDao.findByGoalPeriod(goalPeriod);
    }

    @Override
    public List<OrganisationGoal> findByOwner(String ownerId) throws ValidationFailedException {
        Validate.hasText(ownerId, "Owner ID cannot be null or empty");
        return organisationGoalDao.findByOwner(ownerId);
    }

    @Override
    public List<OrganisationGoal> findOverdueGoals() {
        return organisationGoalDao.findOverdueGoals();
    }

    @Override
    public List<OrganisationGoal> findGoalsWithLowProgress(double threshold) throws ValidationFailedException {
        Validate.isTrue(threshold >= 0 && threshold <= 100, "Threshold must be between 0 and 100");
        return organisationGoalDao.findGoalsWithLowProgress(threshold);
    }

    @Override
    public long countActiveGoals() throws ValidationFailedException {
        try {
            return organisationGoalDao.countActiveGoals();
        } catch (Exception e) {
            throw new ValidationFailedException("Error counting active organisation goals: " + e.getMessage());
        }
    }

    @Override
    public OrganisationGoalMetrics getDashboardMetrics() {
        OrganisationGoalMetrics metrics = new OrganisationGoalMetrics();

        try {
            List<OrganisationGoal> allGoals = findAllActive();
            metrics.setTotalGoals(allGoals.size());

            long activeCount = 0;
            long completedCount = 0;
            long overdueCount = 0;
            BigDecimal totalProgress = BigDecimal.ZERO;

            Date now = new Date();

            for (OrganisationGoal goal : allGoals) {
                if (goal.getProgress().compareTo(new BigDecimal("100")) >= 0) {
                    completedCount++;
                } else {
                    activeCount++;
                }

                if (goal.getEndDate().before(now) && goal.getProgress().compareTo(new BigDecimal("100")) < 0) {
                    overdueCount++;
                }

                totalProgress = totalProgress.add(goal.getProgress());
            }

            metrics.setActiveGoals(activeCount);
            metrics.setCompletedGoals(completedCount);
            metrics.setOverdueGoals(overdueCount);

            if (allGoals.size() > 0) {
                double averageProgress = totalProgress.divide(new BigDecimal(allGoals.size()), 2, RoundingMode.HALF_UP)
                        .doubleValue();
                metrics.setAverageProgress(averageProgress);
            }

        } catch (Exception e) {
            // Log error and return default metrics
            metrics.setTotalGoals(0);
            metrics.setActiveGoals(0);
            metrics.setCompletedGoals(0);
            metrics.setOverdueGoals(0);
            metrics.setAverageProgress(0.0);
        }

        return metrics;
    }

}
