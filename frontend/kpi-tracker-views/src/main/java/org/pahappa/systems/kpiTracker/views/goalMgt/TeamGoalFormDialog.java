package org.pahappa.systems.kpiTracker.views.goalMgt;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.constants.GoalLevel;
import org.pahappa.systems.kpiTracker.core.services.TeamGoalService;
import org.pahappa.systems.kpiTracker.core.services.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.models.goalMgt.TeamGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.DepartmentGoal;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "teamGoalFormDialog")
@SessionScoped
@ViewPath(path = HyperLinks.TEAM_GOAL_FORM_DIALOG)
@Getter
@Setter
public class TeamGoalFormDialog extends DialogForm<TeamGoal> {

    private TeamGoalService teamGoalService;
    private DepartmentGoalService departmentGoalService;
    private UserService userService;

    private List<DepartmentGoal> availableDepartmentGoals;
    private List<User> availableUsers;

    private boolean edit;
    private User owner;
    private Date currentDate;

    public TeamGoalFormDialog() {
        super(HyperLinks.TEAM_GOAL_FORM_DIALOG, 700, 500);
    }

    @PostConstruct
    public void init() {
        teamGoalService = ApplicationContextProvider.getBean(TeamGoalService.class);
        departmentGoalService = ApplicationContextProvider.getBean(DepartmentGoalService.class);
        userService = ApplicationContextProvider.getBean(UserService.class);
        loadAvailableOptions();
    }

    private void loadAvailableOptions() {
        try {
            availableDepartmentGoals = departmentGoalService.findAllActive();
            availableUsers = userService.getUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void persist() throws ValidationFailedException, OperationFailedException {
        if (model == null) {
            model = new TeamGoal();
        }
        // Debug: Check if departmentGoal is set
        if (model.getParentGoal() == null) {
            System.out.println("the goal is empty ");
            throw new ValidationFailedException("Please select a Department Goal before saving.");
        }
        model.setProgress(BigDecimal.ZERO);
        teamGoalService.saveInstance(model);
    }

    @Override
    public void resetModal() {
        super.resetModal();
        model = new TeamGoal();
        edit = false;
        model.setProgress(BigDecimal.ZERO);
        model.setEvaluationTarget(new BigDecimal("100.0"));
        model.setGoalLevel(GoalLevel.TEAM);
        model.setIsActive(true);
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        try {
            this.availableDepartmentGoals = departmentGoalService.findAllActive();
            this.availableUsers = userService.getUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (model != null && model.getId() != null) {
            edit = true;
        } else {
            if (model == null) {
                model = new TeamGoal();
            }
            model.setGoalLevel(GoalLevel.TEAM);
            edit = false;
        }
    }

    public List<DepartmentGoal> getDepartmentGoals() {
        return availableDepartmentGoals;
    }
}
