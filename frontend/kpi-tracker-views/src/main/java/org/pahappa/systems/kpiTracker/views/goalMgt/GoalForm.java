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
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

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
    private List<Department> departments;
    private List<User> users;
    private List<GoalStatus> statuses;

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
            this.departments = departmentService.getAllInstances();
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
        this.goalService.saveInstance(super.model);
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new Goal();
        setEdit(false);
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        if (super.model != null && super.model.getId() != null) {
            setEdit(true);
        } else {
            // If for some reason the model is null, ensure a new one is created.
            if (super.model == null) {
                super.model = new Goal();
            }
            setEdit(false);
        }
    }
}