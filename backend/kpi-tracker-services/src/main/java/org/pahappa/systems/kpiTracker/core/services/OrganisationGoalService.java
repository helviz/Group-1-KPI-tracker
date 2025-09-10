package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.models.goalMgt.OrganisationGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.GoalPeriod;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.exception.OperationFailedException;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface OrganisationGoalService extends GenericService<OrganisationGoal> {

    /**
     * Create a new organisation goal with validation
     */
    OrganisationGoal createOrganisationGoal(OrganisationGoal goal)
            throws ValidationFailedException, OperationFailedException;

    /**
     * Update organisation goal progress and roll up to parent if needed
     */
    OrganisationGoal updateProgress(String goalId, BigDecimal newProgress)
            throws ValidationFailedException, OperationFailedException;

    /**
     * Calculate rollup progress from child department goals
     */
    BigDecimal calculateRollupProgress(OrganisationGoal goal);

    /**
     * Validate that all child department goals contribute exactly 100%
     */
    boolean validateChildGoalsContribution(OrganisationGoal goal);

    /**
     * Find all active organisation goals
     */
    List<OrganisationGoal> findAllActive();

    /**
     * Find organisation goals by goal period
     */
    List<OrganisationGoal> findByGoalPeriod(GoalPeriod goalPeriod) throws ValidationFailedException;

    /**
     * Find organisation goals by owner
     */
    List<OrganisationGoal> findByOwner(String ownerId) throws ValidationFailedException;

    /**
     * Find overdue organisation goals
     */
    List<OrganisationGoal> findOverdueGoals();

    /**
     * Find organisation goals with low progress
     */
    List<OrganisationGoal> findGoalsWithLowProgress(double threshold) throws ValidationFailedException;

    /**
     * Count active organisation goals
     */
    long countActiveGoals() throws ValidationFailedException;

    /**
     * Validate end date against parent goal
     */
    boolean validateEndDate(OrganisationGoal goal, Date parentEndDate) throws ValidationFailedException;

    /**
     * Get dashboard metrics for organisation goals
     */
    OrganisationGoalMetrics getDashboardMetrics();

    /**
     * Inner class for dashboard metrics
     */
    class OrganisationGoalMetrics {
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
