package org.pahappa.systems.kpiTracker.views.goalMgt;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.TeamGoalService;
import org.pahappa.systems.kpiTracker.core.services.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.models.goalMgt.TeamGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.DepartmentGoal;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "teamGoalFormDialog")
@Getter
@Setter
@ViewScoped
public class TeamGoalFormDialog extends DialogForm<TeamGoal> {

    private TeamGoalService teamGoalService;
    private DepartmentGoalService departmentGoalService;
    private UserService userService;

    // Available options for dropdowns
    private List<DepartmentGoal> availableDepartmentGoals;
    private List<User> availableUsers;

    // Form fields
    private String title;
    private String description;
    private String teamName;
    private User owner;
    private Date endDate;
    private BigDecimal evaluationTarget;
    private DepartmentGoal parentGoal;

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
        try {
            if (model == null) {
                model = new TeamGoal();
            }

            // Set form values to model
            model.setTitle(title);
            model.setDescription(description);
            model.setTeamName(teamName);
            model.setOwner(owner);
            //model.setEndDate(endDate);
            model.setEvaluationTarget(evaluationTarget);
            model.setParentGoal(parentGoal);

            // Set audit fields
            if (model.getId() == null) {
                model.setCreatedBy(getLoggedInUser());
                model.setDateCreated(new Date());
                model.setProgress(BigDecimal.ZERO);
                model.setContributionToParent(new BigDecimal("100.0"));
                model.setIsActive(true);
            }

            // Save the goal
            teamGoalService.saveInstance(model);
        } catch (Exception e) {
            throw new OperationFailedException("Failed to save team goal: " + e.getMessage());
        }
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new TeamGoal();
        // Set default values
        super.model.setProgress(BigDecimal.ZERO);
        super.model.setEvaluationTarget(new BigDecimal("100.0"));
        super.model.setContributionToParent(new BigDecimal("100.0"));
        super.model.setIsActive(true);
    }

    @Override
    public void setFormProperties() {
        if (super.model == null) {
            super.model = new TeamGoal();
            super.model.setProgress(BigDecimal.ZERO);
            super.model.setEvaluationTarget(new BigDecimal("100.0"));
            super.model.setContributionToParent(new BigDecimal("100.0"));
            super.model.setIsActive(true);
        }
    }

    private User getLoggedInUser() {
        return SharedAppData.getLoggedInUser();
    }

    public void loadGoal(TeamGoal goal) {
        if (goal != null) {
            super.model = goal;
            // Set form fields from the model
            this.title = goal.getTitle();
            this.description = goal.getDescription();
            this.teamName = goal.getTeamName();
            this.owner = goal.getOwner();
            //this.endDate = goal.getEndDate();
            this.evaluationTarget = goal.getEvaluationTarget();
            this.parentGoal = goal.getParentGoal();
        }
    }

}
