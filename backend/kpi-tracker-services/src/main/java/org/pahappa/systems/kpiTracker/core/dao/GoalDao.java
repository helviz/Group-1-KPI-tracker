package org.pahappa.systems.kpiTracker.core.dao;

import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.models.goalMgt.Goal;

import java.util.List;

public interface GoalDao extends BaseDao<Goal> {
    /**
     * Finds all goals that are assigned to a specific department via the
     * GoalDepartment join table.
     *
     * @param department The department to filter by.
     * @param offset     The starting record for pagination.
     * @param limit      The maximum number of records to return.
     * @return A paginated list of Goals.
     */
    List<Goal> getGoalsForDepartment(Department department, int offset, int limit);

    /**
     * Counts the total number of goals assigned to a specific department.
     *
     * @param department The department to filter by.
     * @return The total count of matching goals.
     */
    int countGoalsForDepartment(Department department);

    /**
     * Get goals filtered by user context (My Goals, My Team, My Department,
     * Organization)
     * 
     * @param context The context filter (MY_GOALS, MY_TEAM, MY_DEPARTMENT,
     *                ORGANIZATION)
     * @param userId  The user ID for filtering
     * @param offset  Pagination offset
     * @param limit   Pagination limit
     * @return List of goals matching the context
     */
    List<Goal> getGoalsByUserContext(String context, String userId, int offset, int limit);

    /**
     * Count goals filtered by user context
     * 
     * @param context The context filter
     * @param userId  The user ID for filtering
     * @return Count of goals matching the context
     */
    int countGoalsByUserContext(String context, String userId);

    /**
     * Get available parent goals for a given goal level
     * 
     * @param goalLevelName The goal level name (e.g., "Team", "Department")
     * @param userId        The user ID for filtering
     * @return List of available parent goals
     */
    List<Goal> getAvailableParentGoals(String goalLevelName, String userId);
}
