package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.models.goalMgt.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.OrganisationGoal;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.ValidationFailedException;

import java.util.List;

public interface DepartmentGoalService extends GenericService<DepartmentGoal> {

    /**
     * Create a new department goal with validation
     */
    DepartmentGoal createDepartmentGoal(DepartmentGoal goal) throws ValidationFailedException;

    /**
     * Update department goal progress and roll up to parent
     */
    DepartmentGoal updateProgress(String goalId, double progress) throws ValidationFailedException;

    /**
     * Calculate rollup progress from team goals
     */
    double calculateRollupProgress(DepartmentGoal goal);

    /**
     * Validate child team goals' contribution percentages
     */
    boolean validateChildContributions(DepartmentGoal goal) throws ValidationFailedException;

    /**
     * Find all active department goals
     */
    List<DepartmentGoal> findAllActive() throws ValidationFailedException;

    /**
     * Find department goals by parent organisation goal
     */
    List<DepartmentGoal> findByParentGoal(OrganisationGoal parentGoal) throws ValidationFailedException;

    /**
     * Find department goals by department name
     */
    List<DepartmentGoal> findByDepartmentName(String departmentName) throws ValidationFailedException;

    /**
     * Find department goals by owner
     */
    List<DepartmentGoal> findByOwner(String ownerId) throws ValidationFailedException;

    /**
     * Find department goals by status
     */
    List<DepartmentGoal> findByStatus(RecordStatus status) throws ValidationFailedException;

    /**
     * Find overdue department goals
     */
    List<DepartmentGoal> findOverdueGoals() throws ValidationFailedException;

    /**
     * Find department goals by title
     */
    List<DepartmentGoal> findByTitleContaining(String title) throws ValidationFailedException;

    /**
     * Count active department goals
     */
    long countActiveGoals() throws ValidationFailedException;

    /**
     * Find department goals with low progress
     */
    List<DepartmentGoal> findGoalsWithLowProgress(double threshold) throws ValidationFailedException;

    /**
     * Find department goals by parent goal and department name
     */
    List<DepartmentGoal> findByParentGoalAndDepartment(OrganisationGoal parentGoal, String departmentName)
            throws ValidationFailedException;

    /**
     * Dashboard metrics for department goals
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
