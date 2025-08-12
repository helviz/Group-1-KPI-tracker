package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.models.team.Team;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TeamServiceImpl extends GenericServiceImpl<Team> implements TeamService {

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

    private boolean isDuplicate(Team entity, String property, Object value) {
        Search search = new Search().addFilterEqual(property, value);
        // If we are UPDATING, we exclude the current entity from the duplicate check.
        if (entity.getId() != null) {
            search.addFilterNotEqual("id", entity.getId());
        }
        return super.count(search) > 0;
    }
}
