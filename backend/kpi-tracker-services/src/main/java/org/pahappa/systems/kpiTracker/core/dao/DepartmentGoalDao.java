package org.pahappa.systems.kpiTracker.core.dao;

import org.pahappa.systems.kpiTracker.models.goalMgt.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.OrganisationGoal;
import org.sers.webutils.model.RecordStatus;

import java.util.List;

public interface DepartmentGoalDao extends BaseDao<DepartmentGoal> {

    /**
     * Find all active department goals
     */
    List<DepartmentGoal> findAllActive();

    /**
     * Find department goals by parent organisation goal
     */
    List<DepartmentGoal> findByParentGoal(OrganisationGoal parentGoal);

    /**
     * Find department goals by department name
     */
    List<DepartmentGoal> findByDepartmentName(String departmentName);

    /**
     * Find department goals by owner
     */
    List<DepartmentGoal> findByOwner(String ownerId);

    /**
     * Find department goals by status
     */
    List<DepartmentGoal> findByStatus(RecordStatus status);

    /**
     * Find department goals that are overdue
     */
    List<DepartmentGoal> findOverdueGoals();

    /**
     * Find department goals by title (partial match)
     */
    List<DepartmentGoal> findByTitleContaining(String title);

    /**
     * Count active department goals
     */
    long countActiveGoals();

    /**
     * Find department goals with progress below threshold
     */
    List<DepartmentGoal> findGoalsWithLowProgress(double threshold);

    /**
     * Find department goals by parent goal and department name
     */
    List<DepartmentGoal> findByParentGoalAndDepartment(OrganisationGoal parentGoal, String departmentName);
}
