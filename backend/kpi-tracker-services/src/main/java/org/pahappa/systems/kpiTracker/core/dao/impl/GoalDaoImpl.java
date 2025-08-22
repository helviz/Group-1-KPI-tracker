package org.pahappa.systems.kpiTracker.core.dao.impl;

import org.pahappa.systems.kpiTracker.core.dao.GoalDao;
import org.pahappa.systems.kpiTracker.models.goal.Goal;
import org.springframework.stereotype.Repository;

@Repository("goalDao")
public class GoalDaoImpl extends BaseDAOImpl<Goal> implements GoalDao {
}