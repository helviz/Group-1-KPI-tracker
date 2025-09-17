package org.pahappa.systems.kpiTracker.core.dao;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.constants.GoalLevel;
import org.pahappa.systems.kpiTracker.models.goalCreation.Goal;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.sers.webutils.model.RecordStatus;

import java.util.List;

public interface GoalDao extends BaseDao<Goal> {

    /**
     * Find goals by level with owner eagerly loaded to avoid
     * LazyInitializationException
     */
    List<Goal> findByLevelWithOwner(GoalLevel goalLevel, RecordStatus recordStatus);

    /**
     * Find goals by owner with owner eagerly loaded to avoid
     * LazyInitializationException
     */
    List<Goal> findByOwnerWithOwner(Staff owner, GoalLevel goalLevel, RecordStatus recordStatus);

    /**
     * Find goals by search criteria with owner eagerly loaded to avoid
     * LazyInitializationException
     */
    List<Goal> findBySearchWithOwner(Search search, int offset, int limit);
}
