package org.pahappa.systems.kpiTracker.views.goalMgt;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.GoalService;
import org.pahappa.systems.kpiTracker.core.services.GoalLevelService;
import org.pahappa.systems.kpiTracker.core.services.GoalPeriodService;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.models.goalMgt.Goal;
import org.pahappa.systems.kpiTracker.models.goalMgt.GoalLevel;
import org.pahappa.systems.kpiTracker.models.goalMgt.GoalPeriod;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.constants.GoalStatus;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.Arrays;
import java.util.List;

@ManagedBean(name = "goalForm")
@SessionScoped
@Getter
@Setter
@ViewPath(path = HyperLinks.GOAL_FORM)
public class GoalForm extends DialogForm<Goal> {
    private static final long serialVersionUID = 1L;
    private GoalService goalService;
    private GoalLevelService goalLevelService;
    private GoalPeriodService goalPeriodService;
    private DepartmentService departmentService;
    private UserService userService;

    private boolean edit;

    // Dropdown data
    private List<GoalLevel> goalLevels;
    private List<GoalPeriod> goalPeriods;
    private List<Department> department;
    private List<User> users;
    private List<GoalStatus> statuses;
    private List<Goal> availableParentGoals;

    @PostConstruct
    public void init() {
        try {
            this.goalService = ApplicationContextProvider.getBean(GoalService.class);
            this.goalLevelService = ApplicationContextProvider.getBean(GoalLevelService.class);
            this.goalPeriodService = ApplicationContextProvider.getBean(GoalPeriodService.class);
            this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
            this.userService = ApplicationContextProvider.getBean(UserService.class);
            this.statuses = Arrays.asList(GoalStatus.values());

            // Preload choices
            this.goalLevels = goalLevelService.getAllInstances();
            this.goalPeriods = goalPeriodService.getAllInstances();
            this.department = departmentService.getAllInstances();
            this.users = userService.getUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GoalForm() {
        super(HyperLinks.GOAL_FORM, 450, 700);
    }

    @Override
    public void persist() throws Exception {
        // Set the current user as owner if not already set
        if (super.model.getOwner() == null) {
            User currentUser = SharedAppData.getLoggedInUser();
            if (currentUser != null) {
                super.model.setOwner(currentUser);
            }
        }

        // Validate that non-Organization goals have a parent
        if (super.model.getGoalLevel() != null &&
                !"Organization".equalsIgnoreCase(super.model.getGoalLevel().getName()) &&
                super.model.getParentGoal() == null) {
            throw new Exception("Parent goal is required for non-Organization goals");
        }

        this.goalService.saveInstance(super.model);
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new Goal();
        setEdit(false);
        this.availableParentGoals = null;
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        this.goalLevels = goalLevelService.getAllInstances();
        this.goalPeriods = goalPeriodService.getAllInstances();
        this.department = departmentService.getAllInstances();
        try {
            this.users = userService.getUsers();
        } catch (OperationFailedException e) {
            throw new RuntimeException(e);
        }

        if (super.model != null && super.model.getId() != null) {
            setEdit(true);
        } else {
            if (super.model == null) {
                super.model = new Goal();
            }
            setEdit(false);
        }
    }

    /**
     * Load available parent goals when goal level changes
     */
    public void loadAvailableParentGoals() {
        if (super.model != null && super.model.getGoalLevel() != null) {
            User currentUser = SharedAppData.getLoggedInUser();
            if (currentUser != null) {
                try {
                    this.availableParentGoals = goalService.getAvailableParentGoals(
                            super.model.getGoalLevel().getName(),
                            currentUser.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Check if the current user can create goals at the selected level
     */
    public boolean canCreateGoalAtLevel(String levelName) {
        User currentUser = SharedAppData.getLoggedInUser();
        if (currentUser == null) {
            return false;
        }

        // Managers can create Organization, Department, and Team goals
        if (isManager(currentUser)) {
            return "Organization".equalsIgnoreCase(levelName) ||
                    "Department".equalsIgnoreCase(levelName) ||
                    "Team".equalsIgnoreCase(levelName);
        }

        // Employees can only create Individual goals
        return "Individual".equalsIgnoreCase(levelName);
    }

    /**
     * Check if the current user is a manager
     */
    private boolean isManager(User user) {
        // This is a simplified check - you should implement proper role checking
        // based on your application's role system
        return user.getRoles() != null && user.getRoles().stream()
                .anyMatch(role -> role.getName().toLowerCase().contains("manager") ||
                        role.getName().toLowerCase().contains("admin"));
    }
}