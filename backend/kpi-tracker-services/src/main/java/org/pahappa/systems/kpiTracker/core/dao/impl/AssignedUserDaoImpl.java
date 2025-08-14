package org.pahappa.systems.kpiTracker.core.dao.impl;

import org.pahappa.systems.kpiTracker.core.dao.AssignedUserDao;
import org.pahappa.systems.kpiTracker.models.user.AssignedUser;
import org.springframework.stereotype.Repository;

@Repository("AssignedUserDAO")
public class AssignedUserDaoImpl extends BaseDAOImpl<AssignedUser> implements AssignedUserDao {
}
