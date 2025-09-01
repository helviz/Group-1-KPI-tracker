package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.dao.IndividualGoalDao;
import org.pahappa.systems.kpiTracker.core.services.IndividualGoalService;
import org.pahappa.systems.kpiTracker.core.services.ProgressCalculationService;
import org.pahappa.systems.kpiTracker.models.goalMgt.IndividualGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.TeamGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.GoalLevel;
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
public class IndividualGoalServiceImpl extends GenericServiceImpl<IndividualGoal> implements IndividualGoalService {

    @Autowired
    private IndividualGoalDao individualGoalDao;

    @Autowired
    private ProgressCalculationService progressCalculationService;

    @Override
    public IndividualGoal saveInstance(IndividualGoal goal) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(goal, "Individual goal cannot be null");
        Validate.hasText(goal.getTitle(), "Goal title is required");
        Validate.notNull(goal.getParentGoal(), "Parent team goal is required");
        Validate.notNull(goal.getOwner(), "Owner is required");
        Validate.hasText(goal.getOwnerName(), "Owner name is required");
        Validate.notNull(goal.getDepartment(), "Department is required");
        Validate.notNull(goal.getTeam(), "Team is required");
        Validate.notNull(goal.getEndDate(), "End date is required");

        // Validate end date is not after parent goal end date
        if (goal.getEndDate().after(goal.getParentGoal().getEndDate())) {
            throw new ValidationFailedException("Individual goal end date cannot be after parent goal end date");
        }

        // Set default values
        goal.setGoalLevel(GoalLevel.INDIVIDUAL);
        goal.setProgress(new java.math.BigDecimal("0.0"));
        goal.setContributionToParent(new java.math.BigDecimal("0.0"));
        goal.setEvaluationTarget(new java.math.BigDecimal("100.0"));
        goal.setIsActive(true);

        return super.save(goal);
    }

    @Override
    public IndividualGoal createIndividualGoal(IndividualGoal goal) throws ValidationFailedException {
        try {
            return saveInstance(goal);
        } catch (OperationFailedException e) {
            throw new ValidationFailedException("Failed to create individual goal: " + e.getMessage());
        }
    }

    @Override
    public IndividualGoal updateProgress(String goalId, double progress) throws ValidationFailedException {
        Validate.hasText(goalId, "Goal ID is required");
        Validate.isTrue(progress >= 0.0 && progress <= 100.0, "Progress must be between 0 and 100");

        IndividualGoal goal = getInstanceByID(goalId);
        Validate.notNull(goal, "Individual goal not found");

        goal.updateProgress(new java.math.BigDecimal(progress));

        // Roll up progress to parent goal
        rollupProgressToParent(goal);

        return super.save(goal);
    }

    @Override
    public double calculateRollupProgress(IndividualGoal goal) {
        // Use the ProgressCalculationService to calculate individual goal progress from
        // KPIs
        return progressCalculationService.calculateIndividualGoalProgress(goal);
    }

    @Override
    public boolean validateContributionToParent(IndividualGoal goal) throws ValidationFailedException {
        if (goal.getContributionToParent() == null) {
            throw new ValidationFailedException("Contribution to parent is required");
        }

        if (goal.getContributionToParent().compareTo(java.math.BigDecimal.ZERO) < 0 ||
                goal.getContributionToParent().compareTo(new java.math.BigDecimal("100")) > 0) {
            throw new ValidationFailedException("Contribution to parent must be between 0 and 100%");
        }

        return true;
    }

    @Override
    public List<IndividualGoal> findAllActive() throws ValidationFailedException {
        try {
            return individualGoalDao.findAllActive();
        } catch (Exception e) {
            throw new ValidationFailedException("Error finding active individual goals: " + e.getMessage());
        }
    }

    @Override
    public List<IndividualGoal> findByParentGoal(TeamGoal parentGoal) throws ValidationFailedException {
        Validate.notNull(parentGoal, "Parent goal is required");
        try {
            return individualGoalDao.findByParentGoal(parentGoal);
        } catch (Exception e) {
            throw new ValidationFailedException("Error finding individual goals by parent: " + e.getMessage());
        }
    }

    @Override
    public List<IndividualGoal> findByOwner(String ownerId) throws ValidationFailedException {
        Validate.hasText(ownerId, "Owner ID is required");
        try {
            return individualGoalDao.findByOwner(ownerId);
        } catch (Exception e) {
            throw new ValidationFailedException("Error finding individual goals by owner: " + e.getMessage());
        }
    }

    @Override
    public List<IndividualGoal> findByOwnerName(String ownerName) throws ValidationFailedException {
        Validate.hasText(ownerName, "Owner name is required");
        try {
            return individualGoalDao.findByOwnerName(ownerName);
        } catch (Exception e) {
            throw new ValidationFailedException("Error finding individual goals by owner name: " + e.getMessage());
        }
    }

    @Override
    public List<IndividualGoal> findByDepartment(String department) throws ValidationFailedException {
        Validate.hasText(department, "Department is required");
        try {
            return individualGoalDao.findByDepartment(department);
        } catch (Exception e) {
            throw new ValidationFailedException("Error finding individual goals by department: " + e.getMessage());
        }
    }

    @Override
    public List<IndividualGoal> findByTeam(String team) throws ValidationFailedException {
        Validate.hasText(team, "Team is required");
        try {
            return individualGoalDao.findByTeam(team);
        } catch (Exception e) {
            throw new ValidationFailedException("Error finding individual goals by team: " + e.getMessage());
        }
    }

    @Override
    public List<IndividualGoal> findByStatus(RecordStatus status) throws ValidationFailedException {
        Validate.notNull(status, "Status is required");
        try {
            return individualGoalDao.findByStatus(status);
        } catch (Exception e) {
            throw new ValidationFailedException("Error finding individual goals by status: " + e.getMessage());
        }
    }

    @Override
    public List<IndividualGoal> findOverdueGoals() throws ValidationFailedException {
        try {
            return individualGoalDao.findOverdueGoals();
        } catch (Exception e) {
            throw new ValidationFailedException("Error finding overdue individual goals: " + e.getMessage());
        }
    }

    @Override
    public List<IndividualGoal> findByTitleContaining(String title) throws ValidationFailedException {
        Validate.hasText(title, "Title is required");
        try {
            return individualGoalDao.findByTitleContaining(title);
        } catch (Exception e) {
            throw new ValidationFailedException("Error finding individual goals by title: " + e.getMessage());
        }
    }

    @Override
    public long countActiveGoals() throws ValidationFailedException {
        try {
            return individualGoalDao.countActiveGoals();
        } catch (Exception e) {
            throw new ValidationFailedException("Error counting active individual goals: " + e.getMessage());
        }
    }

    @Override
    public List<IndividualGoal> findGoalsWithLowProgress(double threshold) throws ValidationFailedException {
        Validate.isTrue(threshold >= 0.0 && threshold <= 100.0, "Threshold must be between 0 and 100");
        try {
            return individualGoalDao.findGoalsWithLowProgress(threshold);
        } catch (Exception e) {
            throw new ValidationFailedException("Error finding individual goals with low progress: " + e.getMessage());
        }
    }

    @Override
    public List<IndividualGoal> findByParentGoalAndOwner(TeamGoal parentGoal, String ownerId)
            throws ValidationFailedException {
        Validate.notNull(parentGoal, "Parent goal is required");
        Validate.hasText(ownerId, "Owner ID is required");
        try {
            return individualGoalDao.findByParentGoalAndOwner(parentGoal, ownerId);
        } catch (Exception e) {
            throw new ValidationFailedException(
                    "Error finding individual goals by parent and owner: " + e.getMessage());
        }
    }

    @Override
    public DashboardMetrics getDashboardMetrics() {
        DashboardMetrics metrics = new DashboardMetrics();

        try {
            List<IndividualGoal> allGoals = findAll();
            List<IndividualGoal> activeGoals = findAllActive();
            List<IndividualGoal> overdueGoals = findOverdueGoals();

            metrics.setTotalGoals(allGoals.size());
            metrics.setActiveGoals(activeGoals.size());
            metrics.setOverdueGoals(overdueGoals.size());

            // Calculate completed goals and average progress
            long completedCount = 0;
            double totalProgress = 0.0;

            for (IndividualGoal goal : allGoals) {
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
    public boolean isDeletable(IndividualGoal instance) throws OperationFailedException {
        // Individual goals are leaf nodes, so they can always be deleted
        return true;
    }

    /**
     * Roll up progress to parent team goal
     */
    private void rollupProgressToParent(IndividualGoal goal) {
        if (goal.getParentGoal() != null) {
            // Use the ProgressCalculationService to roll up progress through the entire
            // hierarchy
            progressCalculationService.rollupProgressFromIndividualGoal(goal);
        }
    }
}
