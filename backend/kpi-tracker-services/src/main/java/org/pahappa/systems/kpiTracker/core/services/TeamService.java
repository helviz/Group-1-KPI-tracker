package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.models.team.Team;

public interface TeamService extends GenericService<Team> {
    Team getTeamByName(String name);
}
