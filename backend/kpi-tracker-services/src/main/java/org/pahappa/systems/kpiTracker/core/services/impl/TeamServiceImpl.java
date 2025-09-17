package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.services.StaffService;
import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.models.team.Team;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.User;
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

    private final StaffService staffService;

    @Autowired
    public TeamServiceImpl(StaffService staffService) {
        this.staffService = staffService;
    }

    @Override
    public Team saveInstance(Team team) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(team, "Team details cannot be null");
        Validate.hasText(team.getTeamName(), "Team name is required");
        Validate.notNull(team.getDepartment(), "Department is required");

        if (isDuplicate(team, "teamName", team.getTeamName())) {
            throw new ValidationFailedException("A team with the same name already exists");
        }
        return super.save(team);
    }


    @Override
    public boolean isDeletable(Team instance) throws OperationFailedException {
        // A team is not deletable if it has members.
        return staffService.getStaffByTeam(instance).isEmpty();
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
    public List<Staff> getStaffWithoutTeamInDepartment(Department department) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(department, "Department cannot be null");

        // Get all staff in the department
        List<Staff> staffInDepartment = staffService.getStaffByDepartment(department);

        // Get all teams in the department
        Search teamSearch = new Search();
        teamSearch.addFilterEqual("department", department);
        List<Team> teamsInDepartment = getInstances(teamSearch, 0, 0);

        // Collect all staff IDs of members who are already in any team in this department
        Set<String> staffIdsInAnyTeam = new HashSet<>();
        for (Team team : teamsInDepartment) {
            List<Staff> staffInTeam = staffService.getStaffByTeam(team);
            for (Staff staff : staffInTeam) {
                staffIdsInAnyTeam.add(staff.getId());
            }
        }

        // Filter the department's staff to find those not in any team, then map to User
        List<Staff> staffWithoutTeam = new ArrayList<>();
        for (Staff staff : staffInDepartment) {
            if (!staffIdsInAnyTeam.contains(staff.getId())) {
                staffWithoutTeam.add(staff);
            }
        }
        return staffWithoutTeam;
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
