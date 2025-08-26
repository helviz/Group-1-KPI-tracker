package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.core.services.GenericService;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.models.team.Team;
import org.pahappa.systems.kpiTracker.models.user.AssignedUser;
import org.sers.webutils.model.security.User;

import java.util.List;

public interface AssignedUserService extends GenericService<AssignedUser> {

    /*Assignment Management*/
    AssignedUser assignUserToDepartment(User user, Department department);

    AssignedUser assignUserToTeams(AssignedUser assignedUser, List<Team> teams);

    AssignedUser removeUserFromTeam(AssignedUser assignedUser, Team team);

    AssignedUser removeUserFromDepartment(AssignedUser assignedUser);

    AssignedUser clearUserTeams(AssignedUser assignedUser);

    AssignedUser findAssignedUserByUser(User user);
    // ---------------------------
    // Retrieval Queries
    // ---------------------------

    List<AssignedUser> getAssignedUsersByDepartment(Department department);

    List<AssignedUser> getAssignedUsersByTeam(Team team);

    List<Team> getTeamsForUser(User user);

    Department getDepartmentForUser(User user);

    // ---------------------------
    // Batch Operations
    // ---------------------------
    void assignMultipleUsersToDepartment(List<User> users, Department department);

    void assignMultipleUsersToTeam(List<AssignedUser> assignedUsers, Team team);

}
