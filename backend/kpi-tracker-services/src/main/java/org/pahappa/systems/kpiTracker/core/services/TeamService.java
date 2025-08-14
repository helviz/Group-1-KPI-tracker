package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.models.team.Team;
import org.sers.webutils.model.security.User;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;

import java.util.List;

public interface TeamService extends GenericService<Team> {
    Team getTeamByName(String name);

    List<User> getUsersWithoutTeamInDepartment(Department department) throws ValidationFailedException, OperationFailedException;
}
