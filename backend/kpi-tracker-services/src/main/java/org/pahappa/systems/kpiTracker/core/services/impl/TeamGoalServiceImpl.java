package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.dao.TeamGoalDao;
import org.pahappa.systems.kpiTracker.core.services.TeamGoalService;
import org.pahappa.systems.kpiTracker.models.goalMgt.TeamGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.IndividualGoal;
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
public class TeamGoalServiceImpl extends GenericServiceImpl<TeamGoal> implements TeamGoalService {

    @Autowired
    private TeamGoalDao teamGoalDao;

    @Override
    public TeamGoal saveInstance(TeamGoal goal) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(goal, "Team goal cannot be null");
        Validate.hasText(goal.getTitle(), "Goal title is required");
        Validate.notNull(goal.getParentGoal(), "Parent department goal is required");
        Validate.hasText(goal.getTeamName(), "Team name is required");
        Validate.notNull(goal.getEndDate(), "End date is required");
        Validate.notNull(goal.getOwner(), "Owner is required");

        // Validate end date is not after parent goal end date
        if (goal.getEndDate().after(goal.getParentGoal().getEndDate())) {
            throw new ValidationFailedException("Team goal end date cannot be after parent goal end date");
        }

        // Set default values
        goal.setGoalLevel(GoalLevel.TEAM);
        goal.setProgress(new java.math.BigDecimal("0.0"));
        goal.setContributionToParent(new java.math.BigDecimal("0.0"));
        goal.setEvaluationTarget(new java.math.BigDecimal("100.0"));
        goal.setIsActive(true);

        return super.save(goal);
    }

    @Override
    public TeamGoal createTeamGoal(TeamGoal goal) throws ValidationFailedException {
        try {
            return saveInstance(goal);
        } catch (OperationFailedException e) {
            throw new ValidationFailedException("Failed to create team goal: " + e.getMessage());
        }
    }

    @Override
    public TeamGoal updateProgress(String goalId, double progress) throws ValidationFailedException {
        Validate.hasText(goalId, "Goal ID is required");
        Validate.isTrue(progress >= 0.0 && progress <= 100.0, "Progress must be between 0 and 100");

        TeamGoal goal = getInstanceByID(goalId);
        Validate.notNull(goal, "Team goal not found");

        goal.updateProgress(new java.math.BigDecimal(progress));

        // Roll up progress to parent goal
        rollupProgressToParent(goal);

        return super.save(goal);
    }

    @Override
    public double calculateRollupProgress(TeamGoal goal) {
        if (goal.getIndividualGoals() == null || goal.getIndividualGoals().isEmpty()) {
            return goal.getProgress().doubleValue();
        }

        double totalContribution = 0.0;
        double weightedProgress = 0.0;

        for (IndividualGoal individualGoal : goal.getIndividualGoals()) {
            if (individualGoal.getIsActive() && individualGoal.getContributionToParent() != null) {
                totalContribution += individualGoal.getContributionToParent().doubleValue();
                weightedProgress += (individualGoal.getProgress().doubleValue()
                        * individualGoal.getContributionToParent().doubleValue() / 100.0);
            }
        }

        // Validate total contribution equals 100%
        if (Math.abs(totalContribution - 100.0) > 0.01) {
            return goal.getProgress().doubleValue(); // Return current progress if validation fails
        }

        return weightedProgress;
    }

    @Override
    public boolean validateChildContributions(TeamGoal goal) throws ValidationFailedException {
        if (goal.getIndividualGoals() == null || goal.getIndividualGoals().isEmpty()) {
            return true;
        }

        double totalContribution = 0.0;
        for (IndividualGoal individualGoal : goal.getIndividualGoals()) {
            if (individualGoal.getIsActive() && individualGoal.getContributionToParent() != null) {
                totalContribution += individualGoal.getContributionToParent().doubleValue();
            }
        }

        if (Math.abs(totalContribution - 100.0) > 0.01) {
            throw new ValidationFailedException(
                    "Total contribution from individual goals must equal 100%. Current total: " + totalContribution
                            + "%");
        }

        return true;
    }

    @Override
    public List<TeamGoal> findAllActive() throws ValidationFailedException {
        try {
            return teamGoalDao.findAllActive();
        } catch (Exception e) {
            throw new ValidationFailedException("Error finding active team goals: " + e.getMessage());
        }
    }

    @Override
    public List<TeamGoal> findByParentGoal(DepartmentGoal parentGoal) throws ValidationFailedException {
        Validate.notNull(parentGoal, "Parent goal is required");
        try {
            return teamGoalDao.findByParentGoal(parentGoal);
        } catch (Exception e) {
            throw new ValidationFailedException("Error finding team goals by parent: " + e.getMessage());
        }
    }

    @Override
    public List<TeamGoal> findByTeamName(String teamName) throws ValidationFailedException {
        Validate.hasText(teamName, "Team name is required");
        try {
            return teamGoalDao.findByTeamName(teamName);
        } catch (Exception e) {
            throw new ValidationFailedException("Error finding team goals by team name: " + e.getMessage());
        }
    }

    @Override
    public List<TeamGoal> findByOwner(String ownerId) throws ValidationFailedException {
        Validate.hasText(ownerId, "Owner ID is required");
        try {
            return teamGoalDao.findByOwner(ownerId);
        } catch (Exception e) {
            throw new ValidationFailedException("Error finding team goals by owner: " + e.getMessage());
        }
    }

    @Override
    public List<TeamGoal> findByStatus(RecordStatus status) throws ValidationFailedException {
        Validate.notNull(status, "Status is required");
        try {
            return teamGoalDao.findByStatus(status);
        } catch (Exception e) {
            throw new ValidationFailedException("Error finding team goals by status: " + e.getMessage());
        }
    }

    @Override
    public List<TeamGoal> findOverdueGoals() throws ValidationFailedException {
        try {
            return teamGoalDao.findOverdueGoals();
        } catch (Exception e) {
            throw new ValidationFailedException("Error finding overdue team goals: " + e.getMessage());
        }
    }

    @Override
    public List<TeamGoal> findByTitleContaining(String title) throws ValidationFailedException {
        Validate.hasText(title, "Title is required");
        try {
            return teamGoalDao.findByTitleContaining(title);
        } catch (Exception e) {
            throw new ValidationFailedException("Error finding team goals by title: " + e.getMessage());
        }
    }

    @Override
    public long countActiveGoals() throws ValidationFailedException {
        try {
            return teamGoalDao.countActiveGoals();
        } catch (Exception e) {
            throw new ValidationFailedException("Error counting active team goals: " + e.getMessage());
        }
    }

    @Override
    public List<TeamGoal> findGoalsWithLowProgress(double threshold) throws ValidationFailedException {
        Validate.isTrue(threshold >= 0.0 && threshold <= 100.0, "Threshold must be between 0 and 100");
        try {
            return teamGoalDao.findGoalsWithLowProgress(threshold);
        } catch (Exception e) {
            throw new ValidationFailedException("Error finding team goals with low progress: " + e.getMessage());
        }
    }

    @Override
    public List<TeamGoal> findByParentGoalAndTeam(DepartmentGoal parentGoal, String teamName)
            throws ValidationFailedException {
        Validate.notNull(parentGoal, "Parent goal is required");
        Validate.hasText(teamName, "Team name is required");
        try {
            return teamGoalDao.findByParentGoalAndTeam(parentGoal, teamName);
        } catch (Exception e) {
            throw new ValidationFailedException("Error finding team goals by parent and team: " + e.getMessage());
        }
    }

    @Override
    public DashboardMetrics getDashboardMetrics() {
        DashboardMetrics metrics = new DashboardMetrics();

        try {
            List<TeamGoal> allGoals = findAll();
            List<TeamGoal> activeGoals = findAllActive();
            List<TeamGoal> overdueGoals = findOverdueGoals();

            metrics.setTotalGoals(allGoals.size());
            metrics.setActiveGoals(activeGoals.size());
            metrics.setOverdueGoals(overdueGoals.size());

            // Calculate completed goals and average progress
            long completedCount = 0;
            double totalProgress = 0.0;

            for (TeamGoal goal : allGoals) {
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
    public boolean isDeletable(TeamGoal instance) throws OperationFailedException {
        // Check if goal has child individual goals
        if (instance.getIndividualGoals() != null && !instance.getIndividualGoals().isEmpty()) {
            return false; // Cannot delete if it has child goals
        }
        return true;
    }

    /**
     * Roll up progress to parent department goal
     */
    private void rollupProgressToParent(TeamGoal goal) {
        if (goal.getParentGoal() != null) {
            // This would typically call the DepartmentGoalService to update its progress
            // For now, we'll just calculate the rollup progress
            double rollupProgress = calculateRollupProgress(goal);
            goal.updateProgress(new java.math.BigDecimal(rollupProgress));
        }
    }
}
