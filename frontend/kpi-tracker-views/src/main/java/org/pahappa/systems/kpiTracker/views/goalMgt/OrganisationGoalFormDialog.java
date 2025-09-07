package org.pahappa.systems.kpiTracker.views.goalMgt;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.GoalPeriodService;
import org.pahappa.systems.kpiTracker.core.services.OrganisationGoalService;
import org.pahappa.systems.kpiTracker.models.goalMgt.GoalPeriod;
import org.pahappa.systems.kpiTracker.models.goalMgt.OrganisationGoal;
import org.pahappa.systems.kpiTracker.constants.GoalLevel;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.goalMgt.OrganisationGoalView;
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
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@ManagedBean(name = "organisationGoalFormDialog")
@Getter
@Setter
@ViewPath(path = HyperLinks.ORGANISATION_GOAL_FORM_DIALOG)
@SessionScoped
public class OrganisationGoalFormDialog extends DialogForm<OrganisationGoal> {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(OrganisationGoalFormDialog.class.getName());

    private OrganisationGoalService organisationGoalService;
    private GoalPeriodService goalPeriodService;
    private UserService userService;
    private List<User> availableUsers;
    private List<GoalPeriod> goalPeriods;

    private boolean edit;
    private User owner;
    public Date currentDate;

    @PostConstruct
    public void init() {
        try {
            this.organisationGoalService = ApplicationContextProvider.getBean(OrganisationGoalService.class);
            this.userService = ApplicationContextProvider.getBean(UserService.class);
            this.goalPeriodService = ApplicationContextProvider.getBean(GoalPeriodService.class);
            this.currentDate = new Date();// current date initialisation
            this.goalPeriods = goalPeriodService.getAllInstances();
            this.availableUsers = this.userService.getUsers();
            this.owner = SharedAppData.getLoggedInUser();
            LOGGER.info("OrganisationGoalFormDialog initialized with " + goalPeriods.size() + " periods and "
                    + availableUsers.size() + " users");
        } catch (Exception e) {
            LOGGER.severe("Failed to initialize OrganisationGoalFormDialog: " + e.getMessage());
        }
    }

    public OrganisationGoalFormDialog() {
        super(HyperLinks.ORGANISATION_GOAL_FORM_DIALOG, 600, 650);
    }

    @Override
    public void persist() throws ValidationFailedException, OperationFailedException {
        try {
            if (super.model == null) {
                super.model = new OrganisationGoal();
            }

            // Set form values to model
            super.model.setOwner(owner);

            // Validate that goal period is selected (dates will be inherited from goal
            // period)
            if (super.model.getGoalPeriod() == null) {
                throw new ValidationFailedException("Goal period is required");
            }

            // Set audit fields for new records
            if (super.model.getId() == null) {
                super.model.setCreatedBy(SharedAppData.getLoggedInUser());
                super.model.setDateCreated(new Date());
                super.model.setProgress(BigDecimal.ZERO);
                super.model.setContributionToParent(new BigDecimal("100.0"));
                super.model.setIsActive(true);
                // Ensure goal level is set for organisation goals
                super.model.setGoalLevel(GoalLevel.ORGANISATION);
            }

            // Save the goal
            this.organisationGoalService.saveInstance(super.model);

            // Refresh the parent view data
            refreshParentView();
        } catch (Exception e) {
            throw new OperationFailedException("Failed to save organisation goal: " + e.getMessage());
        }
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new OrganisationGoal();
        setEdit(false);
        super.model.setProgress(BigDecimal.ZERO);
        super.model.setEvaluationTarget(new BigDecimal("100.0"));
        super.model.setContributionToParent(new BigDecimal("100.0"));
        super.model.setIsActive(true);
        // Don't set endDate here - let user select it (must be in future per @Future
        // constraint)
        super.model.setGoalLevel(GoalLevel.ORGANISATION); // Ensure goal level is set
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        try {
            this.availableUsers = this.userService.getUsers();
            this.goalPeriods = goalPeriodService.getAllInstances();
            this.owner = SharedAppData.getLoggedInUser();
        } catch (OperationFailedException e) {
            LOGGER.severe("Failed to load form properties: " + e.getMessage());
        }

        if (super.model != null && super.model.getId() != null) {
            setEdit(true);
        } else {
            if (super.model == null) {
                super.model = new OrganisationGoal();
            }
            super.model.setGoalLevel(GoalLevel.ORGANISATION); // Ensure goal level is set
            setEdit(false);
        }
    }

    public Date getCurrentDate() {
        return currentDate != null ? currentDate : new Date(); // Return current date if null
    }

    public String getGoalPeriodStartDate() {
        try {
            if (super.model != null && super.model.getGoalPeriod() != null) {
                Date startDate = super.model.getGoalPeriod().getStartDate();
                return startDate != null ? startDate.toString() : "Not set";
            }
        } catch (Exception e) {
            // Handle lazy initialization or other exceptions
        }
        return "Select a period";
    }

    public String getGoalPeriodEndDate() {
        try {
            if (super.model != null && super.model.getGoalPeriod() != null) {
                Date endDate = super.model.getGoalPeriod().getEndDate();
                return endDate != null ? endDate.toString() : "Not set";
            }
        } catch (Exception e) {
            // Handle lazy initialization or other exceptions
        }
        return "Select a period";
    }

    private void refreshParentView() {
        try {
            // Get the OrganisationGoalView bean and refresh its data
            OrganisationGoalView parentView = ApplicationContextProvider.getBean(OrganisationGoalView.class);
            if (parentView != null) {
                parentView.refreshData();
            }
        } catch (Exception e) {
            LOGGER.warning("Failed to refresh parent view: " + e.getMessage());
        }
    }

}