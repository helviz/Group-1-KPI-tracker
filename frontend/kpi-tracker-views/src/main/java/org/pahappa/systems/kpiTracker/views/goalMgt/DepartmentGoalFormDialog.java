package org.pahappa.systems.kpiTracker.views.goalMgt;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.constants.GoalLevel;
import org.pahappa.systems.kpiTracker.core.services.AssignedUserService;
import org.pahappa.systems.kpiTracker.core.services.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.core.services.OrganisationGoalService;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.models.goalMgt.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.OrganisationGoal;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@ManagedBean(name = "departmentGoalFormDialog")
@Getter
@Setter
@ViewPath(path = HyperLinks.DEPARTMENT_GOAL_FORM_DIALOG)
@SessionScoped
public class DepartmentGoalFormDialog extends DialogForm<DepartmentGoal> {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(DepartmentGoalFormDialog.class.getName());

    private DepartmentGoalService departmentGoalService;
    private OrganisationGoalService organisationGoalService;
    private UserService userService;
    private AssignedUserService assignedUserService;

    // Available options for dropdowns
    private List<OrganisationGoal> availableOrganisationGoals;

    private boolean edit;
    private User owner;
    public Date currentDate;

    public DepartmentGoalFormDialog() {
        super(HyperLinks.DEPARTMENT_GOAL_FORM_DIALOG, 700, 500);
    }

    @PostConstruct
    public void init() {
        try {
            this.departmentGoalService = ApplicationContextProvider.getBean(DepartmentGoalService.class);
            this.organisationGoalService = ApplicationContextProvider.getBean(OrganisationGoalService.class);
            this.userService = ApplicationContextProvider.getBean(UserService.class);
            this.assignedUserService = ApplicationContextProvider.getBean(AssignedUserService.class);
            this.currentDate = new Date(); // current date initialisation
            this.availableOrganisationGoals = organisationGoalService.findAllActive();
            this.owner = SharedAppData.getLoggedInUser();
            LOGGER.info("DepartmentGoalFormDialog initialized with " + availableOrganisationGoals.size()
                    + " organisation goals for user: " + (owner != null ? owner.getUsername() : "No user"));
        } catch (Exception e) {
            LOGGER.severe("Failed to initialize DepartmentGoalFormDialog: " + e.getMessage());
        }
    }

    @Override
    public void persist() throws ValidationFailedException, OperationFailedException {
        try {
            if (super.model == null) {
                super.model = new DepartmentGoal();
            }

            // Set form values to model
            super.model.setOwner(owner);

            // Automatically set department name from logged-in user's department
            String userDepartmentName = getUserDepartmentName();
            if (!"No Department Assigned".equals(userDepartmentName)) {
                super.model.setDepartmentName(userDepartmentName);
                LOGGER.info("Set department name to: " + userDepartmentName);
            } else {
                throw new ValidationFailedException("User must be assigned to a department to create department goals");
            }

            // Log the parent goal for debugging
            LOGGER.info("Parent goal before validation: "
                    + (super.model.getParentGoal() != null ? super.model.getParentGoal().getTitle() : "null"));

            // Validate that parent goal is selected
            if (super.model.getParentGoal() == null) {
                throw new ValidationFailedException("Parent organisation goal is required");
            }

            // Set audit fields for new records
            if (super.model.getId() == null) {
                super.model.setCreatedBy(SharedAppData.getLoggedInUser());
                super.model.setDateCreated(new Date());
                super.model.setProgress(BigDecimal.ZERO);
                super.model.setIsActive(true);
                super.model.setGoalLevel(GoalLevel.DEPARTMENT);
            }

            // Save the goal
            this.departmentGoalService.saveInstance(super.model);
        } catch (Exception e) {
            LOGGER.severe("Error saving department goal: " + e.getMessage());
            throw new OperationFailedException("Failed to save department goal: " + e.getMessage());
        }
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new DepartmentGoal();
        setEdit(false);
        super.model.setProgress(BigDecimal.ZERO);
        super.model.setEvaluationTarget(new BigDecimal("100.0"));
        // Don't set contributionToParent here - let the service handle it
        super.model.setIsActive(true);
        super.model.setGoalLevel(GoalLevel.DEPARTMENT); // Ensure goal level is set
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        try {
            this.availableOrganisationGoals = organisationGoalService.findAllActive();
            this.owner = SharedAppData.getLoggedInUser();
        } catch (Exception e) {
            LOGGER.severe("Failed to load form properties: " + e.getMessage());
        }

        if (super.model != null && super.model.getId() != null) {
            setEdit(true);
        } else {
            if (super.model == null) {
                super.model = new DepartmentGoal();
            }
            super.model.setGoalLevel(GoalLevel.DEPARTMENT); // Ensure goal level is set
            setEdit(false);
        }
    }

    public Date getCurrentDate() {
        return currentDate != null ? currentDate : new Date(); // Return current date if null
    }

    public String getUserDepartmentName() {
        if (owner == null) {
            return "No Department Assigned";
        }

        try {
            Department department = assignedUserService.getDepartmentForUser(owner);
            if (department != null) {
                return department.getName();
            }
        } catch (Exception e) {
            LOGGER.warning("Failed to get department for user " + owner.getUsername() + ": " + e.getMessage());
        }

        return "No Department Assigned";
    }

    public String getParentGoalTitle() {
        try {
            if (super.model != null && super.model.getParentGoal() != null) {
                return super.model.getParentGoal().getTitle();
            }
        } catch (Exception e) {
            // Handle lazy initialization or other exceptions
        }
        return "Select a parent goal";
    }

    public void loadGoal(DepartmentGoal goal) {
        if (goal != null) {
            super.model = goal;
            setFormProperties();
        }
    }

    /**
     * Parameterless show method for JSF action binding
     */
    public void show() {
        show(new ActionEvent(null));
    }

    // Getters for XHTML binding
    public List<OrganisationGoal> getOrganisationGoals() {
        if (availableOrganisationGoals == null) {
            LOGGER.warning("availableOrganisationGoals is null, reloading...");
            try {
                availableOrganisationGoals = organisationGoalService.findAllActive();
                LOGGER.info("Reloaded " + availableOrganisationGoals.size() + " organisation goals");
            } catch (Exception e) {
                LOGGER.severe("Failed to reload organisation goals: " + e.getMessage());
                availableOrganisationGoals = new ArrayList<>();
            }
        }
        LOGGER.info("Returning " + availableOrganisationGoals.size() + " organisation goals");
        return availableOrganisationGoals;
    }
}
