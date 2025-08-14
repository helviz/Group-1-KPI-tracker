package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.models.team.Team;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class TeamServiceImpl extends GenericServiceImpl<Team> implements TeamService {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Team saveInstance(Team team) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(team, "Team details cannot be null");
        Validate.hasText(team.getTeamName(), "Team name is required");
        Validate.notNull(team.getTeamLead(), "Team must have a team lead");
        Validate.notNull(team.getDepartment(), "Department is required");

        if (isDuplicate(team, "teamName", team.getTeamName())) {
            throw new ValidationFailedException("A team with the same name already exists");
        }
        return super.save(team);
    }


    @Override
    public boolean isDeletable(Team instance) throws OperationFailedException {
        // This is for future logic.
        // if the team has active members or KPIs. For now, we allow it.
        return true;
    }

    @Override
    public Team getTeamByName(String name) {
        return super.searchUniqueByPropertyEqual("teamName", name);
    }

    @Override
    public List<Team> getAllInstances(){
        return super.getAllInstances();
    }

    @Override
    public List<User> getUsersWithoutTeamInDepartment(Department department) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(department, "Department cannot be null"); // Throws ValidationFailedException

        // A more performant approach would be to get users by department if that's possible.
        // For now, we work with what we have.
        List<User> allUsers = userService.getUsers();

        Search teamSearch = new Search();
        teamSearch.addFilterEqual("department", department);
        List<Team> teamsInDepartment = getInstances(teamSearch, 0, 0);

        Set<String> memberIdsInDepartmentTeams = new HashSet<>();
        for (Team team : teamsInDepartment) {
            for (User member : team.getMembers()) {
                memberIdsInDepartmentTeams.add(member.getId());
            }
        }

        List<User> usersWithoutTeam = new ArrayList<>();
        for (User user : allUsers) {
            if (!memberIdsInDepartmentTeams.contains(user.getId())) {
                usersWithoutTeam.add(user);
            }
        }
        return usersWithoutTeam;
    }

    private boolean isDuplicate(Team entity, String property, Object value) {
        Search search = new Search().addFilterEqual(property, value);
        // If we are UPDATING, we exclude the current entity from the duplicate check.
        if (entity.getId() != null) {
            search.addFilterNotEqual("id", entity.getId());
        }
        return super.count(search) > 0;
    }
}
