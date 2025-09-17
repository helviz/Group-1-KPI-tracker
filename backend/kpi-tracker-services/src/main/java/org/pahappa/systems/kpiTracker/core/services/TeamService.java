package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.models.team.Team;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;

import java.util.List;

public interface TeamService extends GenericService<Team> {
    Team getTeamByName(String name);

    List<Staff> getStaffWithoutTeamInDepartment(Department department) throws ValidationFailedException, OperationFailedException;
}
