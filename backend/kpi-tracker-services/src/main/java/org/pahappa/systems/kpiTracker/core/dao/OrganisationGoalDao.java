package org.pahappa.systems.kpiTracker.core.dao;

import org.pahappa.systems.kpiTracker.models.goalMgt.OrganisationGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.GoalPeriod;
import org.sers.webutils.model.RecordStatus;

import java.util.List;

public interface OrganisationGoalDao extends BaseDao<OrganisationGoal> {

    /**
     * Find all active organisation goals
     */
    List<OrganisationGoal> findAllActive();

    /**
     * Find organisation goals by goal period
     */
    List<OrganisationGoal> findByGoalPeriod(GoalPeriod goalPeriod);

    /**
     * Find organisation goals by owner
     */
    List<OrganisationGoal> findByOwner(String ownerId);

    /**
     * Find organisation goals by status
     */
    List<OrganisationGoal> findByStatus(RecordStatus status);

    /**
     * Find organisation goals that are overdue
     */
    List<OrganisationGoal> findOverdueGoals();

    /**
     * Find organisation goals by title (partial match)
     */
    List<OrganisationGoal> findByTitleContaining(String title);

    /**
     * Count active organisation goals
     */
    long countActiveGoals();

    /**
     * Find organisation goals with progress below threshold
     */
    List<OrganisationGoal> findGoalsWithLowProgress(double threshold);
}
