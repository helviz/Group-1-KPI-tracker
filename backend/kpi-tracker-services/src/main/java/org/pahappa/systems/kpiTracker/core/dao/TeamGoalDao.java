package org.pahappa.systems.kpiTracker.core.dao;

import org.pahappa.systems.kpiTracker.models.goalMgt.TeamGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.DepartmentGoal;
import org.sers.webutils.model.RecordStatus;

import java.util.List;

public interface TeamGoalDao extends BaseDao<TeamGoal> {

    /**
     * Find all active team goals
     */
    List<TeamGoal> findAllActive();

    /**
     * Find team goals by parent department goal
     */
    List<TeamGoal> findByParentGoal(DepartmentGoal parentGoal);

    /**
     * Find team goals by team name
     */
    List<TeamGoal> findByTeamName(String teamName);

    /**
     * Find team goals by owner
     */
    List<TeamGoal> findByOwner(String ownerId);

    /**
     * Find team goals by status
     */
    List<TeamGoal> findByStatus(RecordStatus status);

    /**
     * Find team goals that are overdue
     */
    List<TeamGoal> findOverdueGoals();

    /**
     * Find team goals by title (partial match)
     */
    List<TeamGoal> findByTitleContaining(String title);

    /**
     * Count active team goals
     */
    long countActiveGoals();

    /**
     * Find team goals with progress below threshold
     */
    List<TeamGoal> findGoalsWithLowProgress(double threshold);

    /**
     * Find team goals by parent goal and team name
     */
    List<TeamGoal> findByParentGoalAndTeam(DepartmentGoal parentGoal, String teamName);
}
