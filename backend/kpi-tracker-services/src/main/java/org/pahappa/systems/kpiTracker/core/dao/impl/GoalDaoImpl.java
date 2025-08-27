package org.pahappa.systems.kpiTracker.core.dao.impl;

import org.pahappa.systems.kpiTracker.core.dao.GoalDao;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.models.goalMgt.Goal;
import org.sers.webutils.model.RecordStatus;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository("goalDao")
public class GoalDaoImpl extends BaseDAOImpl<Goal> implements GoalDao {

    @Override
    public List<Goal> getGoalsForDepartment(Department department, int offset, int limit) {
        if (department == null) {
            return Collections.emptyList();
        }
        String jpql = "SELECT g FROM Goal g JOIN g.goalDepartments gd WHERE gd.department = :department AND g.recordStatus = :recordStatus";

        return this.entityManager.createQuery(jpql, Goal.class)
                .setParameter("department", department)
                .setParameter("recordStatus", RecordStatus.ACTIVE)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public int countGoalsForDepartment(Department department) {
        if (department == null) {
            return 0;
        }
        String jpql = "SELECT COUNT(g) FROM Goal g JOIN g.goalDepartments gd WHERE gd.department = :department AND g.recordStatus = :recordStatus";

        return this.entityManager.createQuery(jpql, Long.class)
                .setParameter("department", department)
                .setParameter("recordStatus", RecordStatus.ACTIVE)
                .getSingleResult()
                .intValue();
    }

    @Override
    public List<Goal> getGoalsByUserContext(String context, String userId, int offset, int limit) {
        if (userId == null || context == null) {
            return Collections.emptyList();
        }

        String jpql = buildContextQuery(context);
        javax.persistence.TypedQuery<Goal> query = this.entityManager.createQuery(jpql, Goal.class)
                .setParameter("recordStatus", RecordStatus.ACTIVE)
                .setFirstResult(offset)
                .setMaxResults(limit);

        // Set userId parameter for all contexts except ORGANIZATION
        if (!context.equalsIgnoreCase("ORGANIZATION")) {
            query.setParameter("userId", userId);
        }

        return query.getResultList();
    }

    @Override
    public int countGoalsByUserContext(String context, String userId) {
        if (userId == null || context == null) {
            return 0;
        }

        String jpql = buildCountQuery(context);

        javax.persistence.TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("recordStatus", RecordStatus.ACTIVE);

        // Set userId parameter for all contexts except ORGANIZATION
        if (!context.equalsIgnoreCase("ORGANIZATION")) {
            query.setParameter("userId", userId);
        }

        return query.getSingleResult().intValue();
    }

    @Override
    public List<Goal> getAvailableParentGoals(String goalLevelName, String userId) {
        if (goalLevelName == null || userId == null) {
            return Collections.emptyList();
        }

        // Determine the parent level based on the current level
        String parentLevelName = getParentLevelName(goalLevelName);
        if (parentLevelName == null) {
            return Collections.emptyList();
        }

        String jpql = "SELECT g FROM Goal g JOIN FETCH g.goalLevel JOIN FETCH g.owner " +
                "WHERE g.goalLevel.name = :parentLevelName AND g.recordStatus = :recordStatus " +
                "ORDER BY g.goalTitle";

        return this.entityManager.createQuery(jpql, Goal.class)
                .setParameter("parentLevelName", parentLevelName)
                .setParameter("recordStatus", RecordStatus.ACTIVE)
                .getResultList();
    }

    /**
     * Build the JPQL query based on the context.
     * This version assumes a 'team' field has been added to the Goal entity.
     */
    private String buildContextQuery(String context) {
        switch (context.toUpperCase()) {
            case "MY_GOALS":
                return "SELECT g FROM Goal g JOIN FETCH g.goalLevel JOIN FETCH g.owner WHERE g.owner.id = :userId AND g.recordStatus = :recordStatus ORDER BY g.goalTitle";

            // CORRECTED LOGIC FOR MY_TEAM USING THE NEW 'team' FIELD in Goal.java
            case "MY_TEAM":
                return "SELECT g FROM Goal g JOIN FETCH g.goalLevel JOIN FETCH g.owner " +
                        "WHERE g.goalLevel.name = 'Team' AND g.recordStatus = :recordStatus " +
                        "AND g.team IN " +
                        "(SELECT t FROM AssignedUser au JOIN au.assignedTeams t WHERE au.user.id = :userId) " +
                        "ORDER BY g.goalTitle";

            case "MY_DEPARTMENT":
                // This query is correct and working.
                return "SELECT g FROM Goal g JOIN FETCH g.goalLevel JOIN FETCH g.owner JOIN g.goalDepartments gd " +
                        "WHERE g.goalLevel.name = 'Department' AND g.recordStatus = :recordStatus " +
                        "AND gd.department IN " +
                        "(SELECT au.department FROM AssignedUser au WHERE au.user.id = :userId) " +
                        "ORDER BY g.goalTitle";

            case "ORGANIZATION":
                return "SELECT g FROM Goal g JOIN FETCH g.goalLevel JOIN FETCH g.owner " +
                        "WHERE g.goalLevel.name = 'Organization' AND g.recordStatus = :recordStatus " +
                        "ORDER BY g.goalTitle";

            default:
                return "SELECT g FROM Goal g JOIN FETCH g.goalLevel JOIN FETCH g.owner WHERE g.recordStatus = :recordStatus ORDER BY g.goalTitle";
        }
    }

    /**
     * Build the JPQL count query based on the context
     */
    private String buildCountQuery(String context) {
        switch (context.toUpperCase()) {
            case "MY_GOALS":
                return "SELECT COUNT(g) FROM Goal g WHERE g.owner.id = :userId AND g.recordStatus = :recordStatus";

            // CORRECTED LOGIC FOR MY_TEAM COUNT USING THE NEW 'team' FIELD
            case "MY_TEAM":
                return "SELECT COUNT(g) FROM Goal g " +
                        "WHERE g.goalLevel.name = 'Team' AND g.recordStatus = :recordStatus " +
                        "AND g.team IN " +
                        "(SELECT t FROM AssignedUser au JOIN au.assignedTeams t WHERE au.user.id = :userId)";

            case "MY_DEPARTMENT":
                // This query is correct and working.
                return "SELECT COUNT(g) FROM Goal g JOIN g.goalDepartments gd " +
                        "WHERE g.goalLevel.name = 'Department' AND g.recordStatus = :recordStatus " +
                        "AND gd.department IN " +
                        "(SELECT au.department FROM AssignedUser au WHERE au.user.id = :userId)";

            case "ORGANIZATION":
                return "SELECT COUNT(g) FROM Goal g JOIN g.goalLevel gl " +
                        "WHERE gl.name = 'Organization' AND g.recordStatus = :recordStatus";

            default:
                return "SELECT COUNT(g) FROM Goal g WHERE g.recordStatus = :recordStatus";
        }
    }

    /**
     * Get the parent level name based on the current level
     */
    private String getParentLevelName(String currentLevel) {
        switch (currentLevel.toLowerCase()) {
            case "individual":
                return "Team";
            case "team":
                return "Department";
            case "department":
                return "Organization";
            case "organization":
                return null; // No parent for organization level
            default:
                return null;
        }
    }
}