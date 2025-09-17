package org.pahappa.systems.kpiTracker.views.goal;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.constants.GoalLevel;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.GoalPeriodService;
import org.pahappa.systems.kpiTracker.core.services.GoalService;
import org.pahappa.systems.kpiTracker.core.services.StaffService;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.models.goalCreation.Goal;
import org.pahappa.systems.kpiTracker.models.goalMgt.GoalPeriod;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@ManagedBean(name = "goalFormDialog")
@SessionScoped
@ViewPath(path = HyperLinks.GOAL_FORM_DIALOG)
public class GoalFormDialog extends DialogForm<Goal> {
    private GoalService goalService;
    private StaffService staffService;
    private DepartmentService departmentService;
    private GoalPeriodService goalPeriodService;
    private UserService userService;
    private List<User> availableUsers;
    private List<Staff> availableStaff;
    private List<GoalPeriod> goalPeriods;
    private GoalLevel selectedGoalLevel;
    private List<GoalLevel> goalLevels;
    private List<Goal> availableParentGoals;
    private List<Department> availableDepartments;

    private boolean edit;

    /**
     * FIX 1: Added a public, no-argument constructor.
     * JSF requires this to instantiate the bean. Its absence was the
     * root cause of the "JSF1090: Navigation case not resolved" error.
     */
    public GoalFormDialog() {
        super(HyperLinks.GOAL_FORM_DIALOG, 600, 650);
    }

    @PostConstruct
    public void init() {
        try {
            this.goalService = ApplicationContextProvider.getBean(GoalService.class);
            this.staffService = ApplicationContextProvider.getBean(StaffService.class);
            this.userService = ApplicationContextProvider.getBean(UserService.class);
            this.goalPeriodService = ApplicationContextProvider.getBean(GoalPeriodService.class);
            this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
            this.goalPeriods = goalPeriodService.getAllInstances();
            this.availableUsers = this.userService.getUsers();
            this.availableStaff = this.staffService.getAllInstances();
            this.goalLevels = Arrays.asList(GoalLevel.values()); // Initialize goalLevels
            this.availableParentGoals = Collections.emptyList();
            this.availableDepartments = departmentService.getAllInstances();
        } catch (Exception e) {
            // Log the error and initialize with empty collections
            System.err.println("Error initializing GoalFormDialog: " + e.getMessage());
            e.printStackTrace();
            this.goalPeriods = Collections.emptyList();
            this.availableUsers = Collections.emptyList();
            this.availableStaff = Collections.emptyList();
            this.goalLevels = Arrays.asList(GoalLevel.values());
            this.availableParentGoals = Collections.emptyList();
            this.availableDepartments = Collections.emptyList();
        }
    }

    @Override
    public void persist() throws Exception {
        try {
            // Set default values for required fields that might not be set by the form
            if (super.model.getEvaluationTarget() == 0.0) {
                super.model.setEvaluationTarget(100.0); // Default evaluation target
            }

            // // Set default owner for new goals (logged-in user's staff record)
            Staff currentStaff = getLoggedInUser();
            System.out.println("Current staff: " + (currentStaff != null ? currentStaff.getFullName() : "null"));

            if (currentStaff != null) {
                super.model.setOwner(currentStaff);
                System.out.println("Owner set to: " + super.model.getOwner().getFullName());
            }

            // Validate required fields before saving
            if (super.model.getGoalLevel() == null) {
                throw new Exception("Goal level is required");
            }
            if (super.model.getGoalName() == null || super.model.getGoalName().trim().isEmpty()) {
                throw new Exception("Goal name is required");
            }
            if (super.model.getDescription() == null || super.model.getDescription().trim().isEmpty()) {
                throw new Exception("Goal description is required");
            }
            if (super.model.getGoalPeriod() == null) {
                throw new Exception("Goal period is required");
            }
            if (super.model.getOwner() == null) {
                throw new Exception("Goal owner is required");
            }
            System.out.println("Saving goal with owner: " + super.model.getOwner().getFullName());

            // assign organisational goals weight to 100
            if (super.model.getGoalLevel() != null && super.model.getGoalLevel() == GoalLevel.ORGANISATION) {
                super.model.setWeight(100);
            }

            this.goalService.saveInstance(super.model);

            System.out.println("Goal saved successfully!");

        } catch (Exception e) {
            System.err.println("Error in persist method: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new Goal();
        setEdit(false);
        this.selectedGoalLevel = null;
        this.availableParentGoals = Collections.emptyList();
        super.model.setDepartment(null);
        super.model.setWeight(0.0);
        super.model.setDescription(null);
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        try {
            this.availableUsers = this.userService.getUsers();
            this.availableStaff = this.staffService.getAllInstances();
            this.goalPeriods = goalPeriodService.getAllInstances();
        } catch (Exception e) {
            System.err.println("Error loading form properties: " + e.getMessage());
            e.printStackTrace();
        }

        // Set edit flag based on whether model has an ID
        if (super.model != null && super.model.getId() != null) {
            setEdit(true);
        } else {
            setEdit(false);
            // Set default values for new goals
            Staff currentStaff = getLoggedInUser();
            if (currentStaff != null) {
                super.model.setOwner(currentStaff);
            }
            // Set default evaluation target
            super.model.setEvaluationTarget(100.0);
            // Set default progress
            super.model.setProgress(0.0);
        }
    }

    public Staff getLoggedInUser() {
        if (staffService != null) {
            User loggedInUser = SharedAppData.getLoggedInUser();
            System.out.println("Logged in user from SharedAppData: "
                    + (loggedInUser != null ? loggedInUser.getFirstName() + " " + loggedInUser.getLastName() : "null"));
            if (loggedInUser != null) {
                Staff staff = staffService.getStaffByUser(loggedInUser);
                System.out.println("Staff found for user: " + (staff != null ? staff.getFullName() : "null"));
                return staff;
            }
        }
        System.out.println("StaffService is null or no logged in user");
        return null;
    }

    public void onGoalLevelChange() {
        System.out.println("Goal level changed to: " +
                (super.model.getGoalLevel() != null ? super.model.getGoalLevel().getDisplayName() : "null"));

        // Clear dependent fields when goal level changes
        super.model.setParentGoal(null);
        super.model.setDepartment(null);
        super.model.setWeight(0.0);
        super.model.setOwner(null);

        if (super.model.getGoalLevel() != null) {
            // Load available parent goals for non-organisation levels
            if (super.model.getGoalLevel() != GoalLevel.ORGANISATION) {
                this.availableParentGoals = goalService.getPotentialParentGoals(super.model.getGoalLevel());
                System.out.println("Available parent goals: " + this.availableParentGoals.size());
            } else {
                this.availableParentGoals = Collections.emptyList();
            }

            // Set default owner for individual goals
            if (super.model.getGoalLevel() == GoalLevel.INDIVIDUAL) {
                Staff currentStaff = getLoggedInUser();
                if (currentStaff != null) {
                    super.model.setOwner(currentStaff);
                    System.out.println("Set default owner: " + currentStaff.getFullName());
                }
            }
        } else {
            this.availableParentGoals = Collections.emptyList();
        }
    }

    public void onParentGoalChange() {
        if ((super.model.getGoalLevel() == GoalLevel.TEAM || super.model.getGoalLevel() == GoalLevel.INDIVIDUAL)
                && super.model.getParentGoal() != null) {
            super.model.setDepartment(super.model.getParentGoal().getDepartment());
        } else {
            super.model.setDepartment(null);
        }
    }

    public boolean isDepartmentApplicable() {
        return super.model.getGoalLevel() != null && super.model.getGoalLevel() != GoalLevel.ORGANISATION;
    }

    public boolean isDepartmentEditable() {
        return super.model.getGoalLevel() != null && super.model.getGoalLevel() != GoalLevel.TEAM
                && super.model.getGoalLevel() != GoalLevel.INDIVIDUAL;
    }

    public boolean isParentApplicable() {
        return super.model.getGoalLevel() != null && super.model.getGoalLevel() != GoalLevel.ORGANISATION;
    }

    public boolean isWeightApplicable() {
        return isParentApplicable();
    }

    public boolean isOwnerApplicable() {
        return super.model.getGoalLevel() != null && super.model.getGoalLevel() == GoalLevel.INDIVIDUAL;
    }
}