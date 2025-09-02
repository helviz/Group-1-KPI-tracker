package org.pahappa.systems.kpiTracker.core.dao;

import org.pahappa.systems.kpiTracker.models.goalMgt.IndividualGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.TeamGoal;
import org.sers.webutils.model.RecordStatus;

import java.util.List;

public interface IndividualGoalDao extends BaseDao<IndividualGoal> {

    /**
     * Find all active individual goals
     */
    List<IndividualGoal> findAllActive();

    /**
     * Find individual goals by parent team goal
     */
    List<IndividualGoal> findByParentGoal(TeamGoal parentGoal);

    /**
     * Find individual goals by owner
     */
    List<IndividualGoal> findByOwner(String ownerId);

    /**
     * Find individual goals by owner name
     */
    List<IndividualGoal> findByOwnerName(String ownerName);

    /**
     * Find individual goals by department
     */
    List<IndividualGoal> findByDepartment(String department);

    /**
     * Find individual goals by team
     */
    List<IndividualGoal> findByTeam(String team);

    /**
     * Find individual goals by status
     */
    List<IndividualGoal> findByStatus(RecordStatus status);

    /**
     * Find individual goals that are overdue
     */
    List<IndividualGoal> findOverdueGoals();

    /**
     * Find individual goals by title (partial match)
     */
    List<IndividualGoal> findByTitleContaining(String title);

    /**
     * Count active individual goals
     */
    long countActiveGoals();

    /**
     * Find individual goals with progress below threshold
     */
    List<IndividualGoal> findGoalsWithLowProgress(double threshold);

    /**
     * Find individual goals by parent goal and owner
     */
    List<IndividualGoal> findByParentGoalAndOwner(TeamGoal parentGoal, String ownerId);
}
