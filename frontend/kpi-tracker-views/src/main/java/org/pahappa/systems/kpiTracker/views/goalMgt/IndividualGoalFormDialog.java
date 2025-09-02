package org.pahappa.systems.kpiTracker.views.goalMgt;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.IndividualGoalService;
import org.pahappa.systems.kpiTracker.core.services.TeamGoalService;
import org.pahappa.systems.kpiTracker.models.goalMgt.IndividualGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.TeamGoal;
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

@ManagedBean(name = "individualGoalFormDialog")
@Getter
@Setter
@ViewScoped
public class IndividualGoalFormDialog extends DialogForm<IndividualGoal> {

    private IndividualGoalService individualGoalService;
    private TeamGoalService teamGoalService;
    private UserService userService;

    // Available options for dropdowns
    private List<TeamGoal> availableTeamGoals;
    private List<User> availableUsers;

    // Form fields
    private String title;
    private String description;
    private User owner;
    private Date endDate;
    private BigDecimal evaluationTarget;
    private TeamGoal parentGoal;

    public IndividualGoalFormDialog() {
        super(HyperLinks.INDIVIDUAL_GOAL_FORM_DIALOG, 700, 500);
    }

    @PostConstruct
    public void init() {
        individualGoalService = ApplicationContextProvider.getBean(IndividualGoalService.class);
        teamGoalService = ApplicationContextProvider.getBean(TeamGoalService.class);
        userService = ApplicationContextProvider.getBean(UserService.class);
        loadAvailableOptions();
    }

    private void loadAvailableOptions() {
        try {
            availableTeamGoals = teamGoalService.findAllActive();
            availableUsers = userService.getUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void persist() throws ValidationFailedException, OperationFailedException {
        try {
            if (model == null) {
                model = new IndividualGoal();
            }

            // Set form values to model
            model.setTitle(title);
            model.setDescription(description);
            model.setOwner(owner);
            model.setEndDate(endDate);
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
            individualGoalService.saveInstance(model);
        } catch (Exception e) {
            throw new OperationFailedException("Failed to save individual goal: " + e.getMessage());
        }
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new IndividualGoal();
        // Set default values
        super.model.setProgress(BigDecimal.ZERO);
        super.model.setEvaluationTarget(new BigDecimal("100.0"));
        super.model.setContributionToParent(new BigDecimal("100.0"));
        super.model.setIsActive(true);
    }

    @Override
    public void setFormProperties() {
        if (super.model == null) {
            super.model = new IndividualGoal();
            super.model.setProgress(BigDecimal.ZERO);
            super.model.setEvaluationTarget(new BigDecimal("100.0"));
            super.model.setContributionToParent(new BigDecimal("100.0"));
            super.model.setIsActive(true);
        }
    }

    private User getLoggedInUser() {
        return SharedAppData.getLoggedInUser();
    }

    public void loadGoal(IndividualGoal goal) {
        if (goal != null) {
            super.model = goal;
            // Set form fields from the model
            this.title = goal.getTitle();
            this.description = goal.getDescription();
            this.owner = goal.getOwner();
            this.endDate = goal.getEndDate();
            this.evaluationTarget = goal.getEvaluationTarget();
            this.parentGoal = goal.getParentGoal();
        }
    }


}
