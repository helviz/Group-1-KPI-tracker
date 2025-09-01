package org.pahappa.systems.kpiTracker.views.goalMgt;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.core.services.OrganisationGoalService;
import org.pahappa.systems.kpiTracker.models.goalMgt.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.OrganisationGoal;
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

@ManagedBean(name = "departmentGoalFormDialog")
@Getter
@Setter
@ViewScoped
public class DepartmentGoalFormDialog extends DialogForm<DepartmentGoal> {

    private DepartmentGoalService departmentGoalService;
    private OrganisationGoalService organisationGoalService;
    private UserService userService;

    // Available options for dropdowns
    private List<OrganisationGoal> availableOrganisationGoals;
    private List<User> availableUsers;

    // Form fields
    private String title;
    private String description;
    private String departmentName;
    private User owner;
    private Date endDate;
    private BigDecimal evaluationTarget;
    private OrganisationGoal parentGoal;

    public DepartmentGoalFormDialog() {
        super(HyperLinks.DEPARTMENT_GOAL_FORM_DIALOG, 700, 500);
    }

    @PostConstruct
    public void init() {
        departmentGoalService = ApplicationContextProvider.getBean(DepartmentGoalService.class);
        organisationGoalService = ApplicationContextProvider.getBean(OrganisationGoalService.class);
        userService = ApplicationContextProvider.getBean(UserService.class);
        loadAvailableOptions();
    }

    private void loadAvailableOptions() {
        try {
            availableOrganisationGoals = organisationGoalService.findAllActive();
            availableUsers = userService.getUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void persist() throws ValidationFailedException, OperationFailedException {
        try {
            if (model == null) {
                model = new DepartmentGoal();
            }

            // Set form values to model
            model.setTitle(title);
            model.setDescription(description);
            model.setDepartmentName(departmentName);
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
            departmentGoalService.saveInstance(model);
        } catch (Exception e) {
            throw new OperationFailedException("Failed to save department goal: " + e.getMessage());
        }
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new DepartmentGoal();
        // Set default values
        super.model.setProgress(BigDecimal.ZERO);
        super.model.setEvaluationTarget(new BigDecimal("100.0"));
        super.model.setContributionToParent(new BigDecimal("100.0"));
        super.model.setIsActive(true);
    }

    @Override
    public void setFormProperties() {
        if (super.model == null) {
            super.model = new DepartmentGoal();
            super.model.setProgress(BigDecimal.ZERO);
            super.model.setEvaluationTarget(new BigDecimal("100.0"));
            super.model.setContributionToParent(new BigDecimal("100.0"));
            super.model.setIsActive(true);
        }
    }

    private User getLoggedInUser() {
        return SharedAppData.getLoggedInUser();
    }

    public void loadGoal(DepartmentGoal goal) {
        if (goal != null) {
            super.model = goal;
            // Set form fields from the model
            this.title = goal.getTitle();
            this.description = goal.getDescription();
            this.departmentName = goal.getDepartmentName();
            this.owner = goal.getOwner();
            this.endDate = goal.getEndDate();
            this.evaluationTarget = goal.getEvaluationTarget();
            this.parentGoal = goal.getParentGoal();
        }
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getEvaluationTarget() {
        return evaluationTarget;
    }

    public void setEvaluationTarget(BigDecimal evaluationTarget) {
        this.evaluationTarget = evaluationTarget;
    }

    public OrganisationGoal getParentGoal() {
        return parentGoal;
    }

    public void setParentGoal(OrganisationGoal parentGoal) {
        this.parentGoal = parentGoal;
    }

    public List<OrganisationGoal> getAvailableOrganisationGoals() {
        return availableOrganisationGoals;
    }

    public void setAvailableOrganisationGoals(List<OrganisationGoal> availableOrganisationGoals) {
        this.availableOrganisationGoals = availableOrganisationGoals;
    }

    public List<User> getAvailableUsers() {
        return availableUsers;
    }

    public void setAvailableUsers(List<User> availableUsers) {
        this.availableUsers = availableUsers;
    }
}
