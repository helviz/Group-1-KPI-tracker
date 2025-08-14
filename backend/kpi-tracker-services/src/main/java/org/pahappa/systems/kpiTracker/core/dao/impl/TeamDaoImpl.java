package org.pahappa.systems.kpiTracker.core.dao.impl;

import org.pahappa.systems.kpiTracker.core.dao.TeamDao;
import org.pahappa.systems.kpiTracker.models.team.Team;
import org.springframework.stereotype.Repository;

@Repository("TeamDAO")
public class TeamDaoImpl extends BaseDAOImpl<Team> implements TeamDao {
}
