package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.models.goalMgt.IndividualGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.TeamGoal;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.ValidationFailedException;

import java.util.List;

public interface IndividualGoalService extends GenericService<IndividualGoal> {

    /**
     * Create a new individual goal with validation
     */
    IndividualGoal createIndividualGoal(IndividualGoal goal) throws ValidationFailedException;

    /**
     * Update individual goal progress and roll up to parent
     */
    IndividualGoal updateProgress(String goalId, double progress) throws ValidationFailedException;

    /**
     * Calculate rollup progress from activities and KPIs
     */
    double calculateRollupProgress(IndividualGoal goal);

    /**
     * Validate individual goal contribution to parent
     */
    boolean validateContributionToParent(IndividualGoal goal) throws ValidationFailedException;

    /**
     * Find all active individual goals
     */
    List<IndividualGoal> findAllActive() throws ValidationFailedException;

    /**
     * Find individual goals by parent team goal
     */
    List<IndividualGoal> findByParentGoal(TeamGoal parentGoal) throws ValidationFailedException;

    /**
     * Find individual goals by owner
     */
    List<IndividualGoal> findByOwner(String ownerId) throws ValidationFailedException;

    /**
     * Find individual goals by owner name
     */
    List<IndividualGoal> findByOwnerName(String ownerName) throws ValidationFailedException;

    /**
     * Find individual goals by department
     */
    List<IndividualGoal> findByDepartment(String department) throws ValidationFailedException;

    /**
     * Find individual goals by team
     */
    List<IndividualGoal> findByTeam(String team) throws ValidationFailedException;

    /**
     * Find individual goals by status
     */
    List<IndividualGoal> findByStatus(RecordStatus status) throws ValidationFailedException;

    /**
     * Find overdue individual goals
     */
    List<IndividualGoal> findOverdueGoals() throws ValidationFailedException;

    /**
     * Find individual goals by title
     */
    List<IndividualGoal> findByTitleContaining(String title) throws ValidationFailedException;

    /**
     * Count active individual goals
     */
    long countActiveGoals() throws ValidationFailedException;

    /**
     * Update owner name from owner details
     */
    void updateOwnerName(IndividualGoal goal) throws ValidationFailedException;

    /**
     * Update department and team information from owner
     */
    void updateDepartmentAndTeam(IndividualGoal goal) throws ValidationFailedException;

    /**
     * Validate end date against parent goal
     */
    boolean validateEndDate(IndividualGoal goal) throws ValidationFailedException;

    /**
     * Find individual goals with low progress
     */
    List<IndividualGoal> findGoalsWithLowProgress(double threshold) throws ValidationFailedException;

    /**
     * Find individual goals by parent goal and owner
     */
    List<IndividualGoal> findByParentGoalAndOwner(TeamGoal parentGoal, String ownerId) throws ValidationFailedException;

    /**
     * Dashboard metrics for individual goals
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
