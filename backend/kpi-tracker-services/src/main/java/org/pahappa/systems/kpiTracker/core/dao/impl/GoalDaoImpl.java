package org.pahappa.systems.kpiTracker.core.dao.impl;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.constants.GoalLevel;
import org.pahappa.systems.kpiTracker.core.dao.GoalDao;
import org.pahappa.systems.kpiTracker.models.goalCreation.Goal;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.sers.webutils.model.RecordStatus;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository("goalDao")
public class GoalDaoImpl extends BaseDAOImpl<Goal> implements GoalDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Goal> findByLevelWithOwner(GoalLevel goalLevel, RecordStatus recordStatus) {
        String jpql = "SELECT g FROM Goal g LEFT JOIN FETCH g.owner WHERE g.goalLevel = :goalLevel AND g.recordStatus = :recordStatus";
        TypedQuery<Goal> query = entityManager.createQuery(jpql, Goal.class);
        query.setParameter("goalLevel", goalLevel);
        query.setParameter("recordStatus", recordStatus);
        return query.getResultList();
    }

    @Override
    public List<Goal> findByOwnerWithOwner(Staff owner, GoalLevel goalLevel, RecordStatus recordStatus) {
        String jpql = "SELECT g FROM Goal g LEFT JOIN FETCH g.owner WHERE g.owner = :owner AND g.goalLevel = :goalLevel AND g.recordStatus = :recordStatus";
        TypedQuery<Goal> query = entityManager.createQuery(jpql, Goal.class);
        query.setParameter("owner", owner);
        query.setParameter("goalLevel", goalLevel);
        query.setParameter("recordStatus", recordStatus);
        return query.getResultList();
    }

    @Override
    public List<Goal> findBySearchWithOwner(Search search, int offset, int limit) {
        // Convert Search to JPQL with JOIN FETCH
        String baseJpql = "SELECT g FROM Goal g LEFT JOIN FETCH g.owner";

        // Build WHERE clause from Search filters
        StringBuilder whereClause = new StringBuilder();
        boolean hasWhere = false;

        // Add basic filters
        if (search.getFilters() != null && !search.getFilters().isEmpty()) {
            for (com.googlecode.genericdao.search.Filter filter : search.getFilters()) {
                if (!hasWhere) {
                    whereClause.append(" WHERE ");
                    hasWhere = true;
                } else {
                    whereClause.append(" AND ");
                }

                // Convert filter to JPQL condition
                String property = filter.getProperty();

                if (filter.getOperator() == com.googlecode.genericdao.search.Filter.OP_EQUAL) {
                    whereClause.append("g.").append(property).append(" = :").append(property.replace(".", "_"));
                } else if (filter.getOperator() == com.googlecode.genericdao.search.Filter.OP_LIKE) {
                    whereClause.append("g.").append(property).append(" LIKE :").append(property.replace(".", "_"));
                }
                // Add more operators as needed
            }
        }

        String jpql = baseJpql + whereClause.toString();

        TypedQuery<Goal> query = entityManager.createQuery(jpql, Goal.class);

        // Set parameters
        if (search.getFilters() != null && !search.getFilters().isEmpty()) {
            for (com.googlecode.genericdao.search.Filter filter : search.getFilters()) {
                String paramName = filter.getProperty().replace(".", "_");
                query.setParameter(paramName, filter.getValue());
            }
        }

        // Set pagination
        query.setFirstResult(offset);
        query.setMaxResults(limit);

        return query.getResultList();
    }
}
