package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.dao.DepartmentGoalDao;
import org.pahappa.systems.kpiTracker.core.services.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.models.goalMgt.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.OrganisationGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.TeamGoal;
import org.pahappa.systems.kpiTracker.constants.GoalLevel;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.exception.OperationFailedException;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DepartmentGoalServiceImpl extends GenericServiceImpl<DepartmentGoal> implements DepartmentGoalService {

    @Autowired
    private DepartmentGoalDao departmentGoalDao;

    @Override
    public DepartmentGoal saveInstance(DepartmentGoal goal) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(goal, "Department goal cannot be null");
        Validate.hasText(goal.getTitle(), "Goal title is required");
        Validate.notNull(goal.getParentGoal(), "Parent organisation goal is required");
        Validate.hasText(goal.getDepartmentName(), "Department name is required");
        //Validate.notNull(goal.getEndDate(), "End date is required");
        Validate.notNull(goal.getOwner(), "Owner is required");

//        // Validate end date is not after parent goal end date
//        if (goal.getEndDate().after(goal.getParentGoal().getEndDate())) {
//            throw new ValidationFailedException("Department goal end date cannot be after parent goal end date");
//        }

        // Set default values
        goal.setGoalLevel(GoalLevel.DEPARTMENT);
        goal.setProgress(new java.math.BigDecimal("0.0"));
        goal.setContributionToParent(new java.math.BigDecimal("0.0"));
        goal.setEvaluationTarget(new java.math.BigDecimal("100.0"));
        // Note: BaseGoal doesn't have setStatus method, status is managed through
        // progress
        goal.setIsActive(true);

        return super.save(goal);
    }

    @Override
    public DepartmentGoal createDepartmentGoal(DepartmentGoal goal) throws ValidationFailedException {
        try {
            return saveInstance(goal);
        } catch (OperationFailedException e) {
            throw new ValidationFailedException("Failed to create department goal: " + e.getMessage());
        }
    }

    @Override
    public DepartmentGoal updateProgress(String goalId, double progress) throws ValidationFailedException {
        Validate.hasText(goalId, "Goal ID is required");
        Validate.isTrue(progress >= 0.0 && progress <= 100.0, "Progress must be between 0 and 100");

        DepartmentGoal goal = getInstanceByID(goalId);
        Validate.notNull(goal, "Department goal not found");

        goal.updateProgress(new java.math.BigDecimal(progress));

        // Note: BaseGoal doesn't have setStatus method, using progress instead
        // Status updates would be handled by the goal entity itself

        // Roll up progress to parent goal
        rollupProgressToParent(goal);

        return save(goal);
    }

    @Override
    public double calculateRollupProgress(DepartmentGoal goal) {
        if (goal.getTeamGoals() == null || goal.getTeamGoals().isEmpty()) {
            return goal.getProgress().doubleValue();
        }

        double totalContribution = 0.0;
        double weightedProgress = 0.0;

        for (TeamGoal teamGoal : goal.getTeamGoals()) {
            if (teamGoal.getIsActive() && teamGoal.getContributionToParent() != null) {
                totalContribution += teamGoal.getContributionToParent().doubleValue();
                weightedProgress += (teamGoal.getProgress().doubleValue()
                        * teamGoal.getContributionToParent().doubleValue() / 100.0);
            }
        }

        // Validate total contribution equals 100%
        if (Math.abs(totalContribution - 100.0) > 0.01) {
            return goal.getProgress().doubleValue(); // Return current progress if validation fails
        }

        return weightedProgress;
    }

    @Override
    public boolean validateChildContributions(DepartmentGoal goal) throws ValidationFailedException {
        if (goal.getTeamGoals() == null || goal.getTeamGoals().isEmpty()) {
            return true;
        }

        double totalContribution = 0.0;
        for (TeamGoal teamGoal : goal.getTeamGoals()) {
            if (teamGoal.getIsActive() && teamGoal.getContributionToParent() != null) {
                totalContribution += teamGoal.getContributionToParent().doubleValue();
            }
        }

        if (Math.abs(totalContribution - 100.0) > 0.01) {
            throw new ValidationFailedException(
                    "Total contribution from team goals must equal 100%. Current total: " + totalContribution + "%");
        }

        return true;
    }

    @Override
    public List<DepartmentGoal> findAllActive() throws ValidationFailedException {
        try {
            return departmentGoalDao.findAllActive();
        } catch (Exception e) {
            throw new ValidationFailedException("Error finding active department goals: " + e.getMessage());
        }
    }

    @Override
    public List<DepartmentGoal> findByParentGoal(OrganisationGoal parentGoal) throws ValidationFailedException {
        Validate.notNull(parentGoal, "Parent goal is required");
        try {
            return departmentGoalDao.findByParentGoal(parentGoal);
        } catch (Exception e) {
            throw new ValidationFailedException("Error finding department goals by parent: " + e.getMessage());
        }
    }

    @Override
    public List<DepartmentGoal> findByDepartmentName(String departmentName) throws ValidationFailedException {
        Validate.hasText(departmentName, "Department name is required");
        try {
            return departmentGoalDao.findByDepartmentName(departmentName);
        } catch (Exception e) {
            throw new ValidationFailedException("Error finding department goals by department name: " + e.getMessage());
        }
    }

    @Override
    public List<DepartmentGoal> findByOwner(String ownerId) throws ValidationFailedException {
        Validate.hasText(ownerId, "Owner ID is required");
        try {
            return departmentGoalDao.findByOwner(ownerId);
        } catch (Exception e) {
            throw new ValidationFailedException("Error finding department goals by owner: " + e.getMessage());
        }
    }

    @Override
    public List<DepartmentGoal> findByStatus(RecordStatus status) throws ValidationFailedException {
        Validate.notNull(status, "Status is required");
        try {
            return departmentGoalDao.findByStatus(status);
        } catch (Exception e) {
            throw new ValidationFailedException("Error finding department goals by status: " + e.getMessage());
        }
    }

    @Override
    public List<DepartmentGoal> findOverdueGoals() throws ValidationFailedException {
        try {
            return departmentGoalDao.findOverdueGoals();
        } catch (Exception e) {
            throw new ValidationFailedException("Error finding overdue department goals: " + e.getMessage());
        }
    }

    @Override
    public List<DepartmentGoal> findByTitleContaining(String title) throws ValidationFailedException {
        Validate.hasText(title, "Title is required");
        try {
            return departmentGoalDao.findByTitleContaining(title);
        } catch (Exception e) {
            throw new ValidationFailedException("Error finding department goals by title: " + e.getMessage());
        }
    }

    @Override
    public long countActiveGoals() throws ValidationFailedException {
        try {
            return departmentGoalDao.countActiveGoals();
        } catch (Exception e) {
            throw new ValidationFailedException("Error counting active department goals: " + e.getMessage());
        }
    }

    @Override
    public List<DepartmentGoal> findGoalsWithLowProgress(double threshold) throws ValidationFailedException {
        Validate.isTrue(threshold >= 0.0 && threshold <= 100.0, "Threshold must be between 0 and 100");
        try {
            return departmentGoalDao.findGoalsWithLowProgress(threshold);
        } catch (Exception e) {
            throw new ValidationFailedException("Error finding department goals with low progress: " + e.getMessage());
        }
    }

    @Override
    public List<DepartmentGoal> findByParentGoalAndDepartment(OrganisationGoal parentGoal, String departmentName)
            throws ValidationFailedException {
        Validate.notNull(parentGoal, "Parent goal is required");
        Validate.hasText(departmentName, "Department name is required");
        try {
            return departmentGoalDao.findByParentGoalAndDepartment(parentGoal, departmentName);
        } catch (Exception e) {
            throw new ValidationFailedException(
                    "Error finding department goals by parent and department: " + e.getMessage());
        }
    }

    @Override
    public DashboardMetrics getDashboardMetrics() {
        DashboardMetrics metrics = new DashboardMetrics();

        try {
            List<DepartmentGoal> allGoals = findAll();
            List<DepartmentGoal> activeGoals = findAllActive();
            List<DepartmentGoal> overdueGoals = findOverdueGoals();

            metrics.setTotalGoals(allGoals.size());
            metrics.setActiveGoals(activeGoals.size());
            metrics.setOverdueGoals(overdueGoals.size());

            // Calculate completed goals and average progress
            long completedCount = 0;
            double totalProgress = 0.0;

            for (DepartmentGoal goal : allGoals) {
                if (goal.getProgress().compareTo(new java.math.BigDecimal("100.0")) >= 0) {
                    completedCount++;
                }
                totalProgress += goal.getProgress().doubleValue();
            }

            metrics.setCompletedGoals(completedCount);
            metrics.setAverageProgress(allGoals.isEmpty() ? 0.0 : totalProgress / allGoals.size());

        } catch (Exception e) {
            // Log error but return default metrics
            metrics.setTotalGoals(0);
            metrics.setActiveGoals(0);
            metrics.setCompletedGoals(0);
            metrics.setOverdueGoals(0);
            metrics.setAverageProgress(0.0);
        }

        return metrics;
    }

    @Override
    public boolean isDeletable(DepartmentGoal instance) throws OperationFailedException {
        // Check if goal has child team goals
        if (instance.getTeamGoals() != null && !instance.getTeamGoals().isEmpty()) {
            return false; // Cannot delete if it has child goals
        }
        return true;
    }

    /**
     * Roll up progress to parent organisation goal
     */
    private void rollupProgressToParent(DepartmentGoal goal) {
        if (goal.getParentGoal() != null) {
            // This would typically call the OrganisationGoalService to update its progress
            // For now, we'll just calculate the rollup progress
            double rollupProgress = calculateRollupProgress(goal);
            goal.updateProgress(new java.math.BigDecimal(rollupProgress));
        }
    }
}
