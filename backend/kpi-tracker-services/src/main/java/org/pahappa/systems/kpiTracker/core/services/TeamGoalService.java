package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.models.goalMgt.TeamGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.DepartmentGoal;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.ValidationFailedException;

import java.util.List;

public interface TeamGoalService extends GenericService<TeamGoal> {

    /**
     * Create a new team goal with validation
     */
    TeamGoal createTeamGoal(TeamGoal goal) throws ValidationFailedException;

    /**
     * Update team goal progress and roll up to parent
     */
    TeamGoal updateProgress(String goalId, double progress) throws ValidationFailedException;

    /**
     * Calculate rollup progress from individual goals
     */
    double calculateRollupProgress(TeamGoal goal);

    /**
     * Validate child individual goals' contribution percentages
     */
    boolean validateChildContributions(TeamGoal goal) throws ValidationFailedException;

    /**
     * Find all active team goals
     */
    List<TeamGoal> findAllActive() throws ValidationFailedException;

    /**
     * Find team goals by parent department goal
     */
    List<TeamGoal> findByParentGoal(DepartmentGoal parentGoal) throws ValidationFailedException;

    /**
     * Find team goals by team name
     */
    List<TeamGoal> findByTeamName(String teamName) throws ValidationFailedException;

    /**
     * Find team goals by owner
     */
    List<TeamGoal> findByOwner(String ownerId) throws ValidationFailedException;

    /**
     * Find team goals by status
     */
    List<TeamGoal> findByStatus(RecordStatus status) throws ValidationFailedException;

    /**
     * Find overdue team goals
     */
    List<TeamGoal> findOverdueGoals() throws ValidationFailedException;

    /**
     * Find team goals by title
     */
    List<TeamGoal> findByTitleContaining(String title) throws ValidationFailedException;

    /**
     * Count active team goals
     */
    long countActiveGoals() throws ValidationFailedException;

    /**
     * Validate end date against parent goal
     */
    boolean validateEndDate(TeamGoal goal) throws ValidationFailedException;

    /**
     * Find team goals with low progress
     */
    List<TeamGoal> findGoalsWithLowProgress(double threshold) throws ValidationFailedException;

    /**
     * Find team goals by parent goal and team name
     */
    List<TeamGoal> findByParentGoalAndTeam(DepartmentGoal parentGoal, String teamName) throws ValidationFailedException;

    /**
     * Dashboard metrics for team goals
     */
    DashboardMetrics getDashboardMetrics();

    /**
     * Inner class for dashboard metrics
     */
    class DashboardMetrics {
        private long totalGoals;
        private long activeGoals;
        private long completedGoals;
        private long overdueGoals;
        private double averageProgress;

        // Getters and setters
        public long getTotalGoals() {
            return totalGoals;
        }

        public void setTotalGoals(long totalGoals) {
            this.totalGoals = totalGoals;
        }

        public long getActiveGoals() {
            return activeGoals;
        }

        public void setActiveGoals(long activeGoals) {
            this.activeGoals = activeGoals;
        }

        public long getCompletedGoals() {
            return completedGoals;
        }

        public void setCompletedGoals(long completedGoals) {
            this.completedGoals = completedGoals;
        }

        public long getOverdueGoals() {
            return overdueGoals;
        }

        public void setOverdueGoals(long overdueGoals) {
            this.overdueGoals = overdueGoals;
        }

        public double getAverageProgress() {
            return averageProgress;
        }

        public void setAverageProgress(double averageProgress) {
            this.averageProgress = averageProgress;
        }
    }
}
