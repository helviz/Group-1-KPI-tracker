package org.pahappa.systems.kpiTracker.core.dao.impl;

import org.pahappa.systems.kpiTracker.core.dao.ActivityDao;
import org.pahappa.systems.kpiTracker.models.activity.Activity;
import org.springframework.stereotype.Repository;

@Repository("activityDao")
public class ActivityDaoImpl extends BaseDAOImpl<Activity> implements ActivityDao {
}