package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.models.team.Team;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.User;
import org.pahappa.systems.kpiTracker.core.services.GenericService;

import java.util.List;

public interface StaffService extends GenericService<Staff> {

    Staff createOrUpdateStaff(User user, Department department) throws ValidationFailedException;

    void assignStaffToTeams(Staff staff, List<Team> teams) throws ValidationFailedException;

    void assignStaffToTeam(Staff staff, Team team) throws ValidationFailedException;

    void removeStaffFromTeam(Staff staff, Team team) throws ValidationFailedException;

    void deactivateStaff(Staff staff) throws ValidationFailedException;

    void activateStaff(Staff staff);

    void clearStaffTeams(Staff staff);

    List<Staff> getStaffByDepartment(Department department);

    List<Staff> getStaffByTeam(Team team);

    List<Team> getTeamsOfStaff(Staff staff);

    Department getDepartmentForUser(User user);

    void createMultipleStaff(List<User> users, Department department);

    void assignMultipleStaffToTeam(List<Staff> staffList, Team team);

    Staff getStaffByUser(User user);

    List<Staff> searchStaffByUsername(String username);

    Staff saveStaff(Staff entityInstance) throws ValidationFailedException, OperationFailedException;

    List<User> getUnassignedUsersForTeam(Team team);

    void clearTeamMembers(Team team);
    List<Staff> getUnassignedStaffInDepartment(Department department);

    Staff createUser(Staff staff) throws ValidationFailedException, OperationFailedException;

}