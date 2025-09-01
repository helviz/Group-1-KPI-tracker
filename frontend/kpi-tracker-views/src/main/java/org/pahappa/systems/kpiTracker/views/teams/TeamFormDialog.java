package org.pahappa.systems.kpiTracker.views.teams;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.AssignedUserService;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.models.team.Team;
import org.pahappa.systems.kpiTracker.models.user.AssignedUser;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ManagedBean(name = "teamFormDialog", eager = true)
@Getter
@Setter
@ViewPath(path = HyperLinks.TEAM_FORM_DIALOG)
@SessionScoped
public class TeamFormDialog extends DialogForm<Team> {

    private static final long serialVersionUID = 1L;
    private TeamService teamService;
    private DepartmentService departmentService;
    private UserService userService;
    private AssignedUserService assignedUserService;
    private List<Department> availableDepartments;
    private Department selectedDepartment;
    private List<User> selectedMembers = new ArrayList<>();
    private List<User> availableUsersForSelect = new ArrayList<>();
    private boolean edit;
    private boolean departmentSelectionDisabled;

    @PostConstruct
    public void init() {
        try {
            this.teamService = ApplicationContextProvider.getBean(TeamService.class);
            this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
            this.userService = ApplicationContextProvider.getBean(UserService.class);
            this.assignedUserService = ApplicationContextProvider.getBean(AssignedUserService.class);
            this.availableDepartments = this.departmentService.getAllInstances();
            resetModal();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TeamFormDialog() {
        super(HyperLinks.TEAM_FORM_DIALOG, 700, 450);
    }

    @Override
    public void persist() throws ValidationFailedException, OperationFailedException {
        Team team = (Team) super.model;
        team.setMembers(new HashSet<User>(this.selectedMembers));
        team.setDepartment(selectedDepartment);
        this.teamService.saveInstance(team);
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new Team();
        this.selectedMembers = new ArrayList<User>();
        this.availableUsersForSelect = new ArrayList<User>();
        this.selectedDepartment = null;
        this.departmentSelectionDisabled =false;
        setEdit(false);
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        if (super.model != null && super.model.getId() != null) {
            setEdit(true);
            Team team = (Team) super.model;
            this.selectedMembers = new ArrayList<User>(team.getMembers());
            this.selectedDepartment = team.getDepartment();
            this.departmentSelectionDisabled = true;
            onDepartmentChange();
        } else {
            if (super.model == null) {
                super.model = new Team();
            }
            setEdit(false);
            this.selectedMembers = new ArrayList<User>();
            this.availableUsersForSelect = new ArrayList<User>();
            this.selectedDepartment = null;
            this.departmentSelectionDisabled = false;
        }
    }

    public void onDepartmentChange() {
        if (this.selectedDepartment != null) {
            try {
                List<AssignedUser> assignedUsersInDepartment = assignedUserService
                        .getAssignedUsersByDepartment(this.selectedDepartment);
                List<User> users = new ArrayList<>();
                for (AssignedUser assignedUser : assignedUsersInDepartment) {
                    users.add(assignedUser.getUser());
                }
                this.availableUsersForSelect = users;
            } catch (Exception e) {
                System.err.println("An error occurred while fetching users for department.");
                e.printStackTrace();
                this.availableUsersForSelect = new ArrayList<User>();
            }
        } else {
            this.availableUsersForSelect = new ArrayList<User>();
        }
    }

    public void prepareNew(Department department) {
        resetModal();
        if (super.model == null) {
            super.model = new Team();
        }
        ((Team) super.model).setDepartment(department);
        this.selectedDepartment = department;
        this.departmentSelectionDisabled = true;
        if (this.selectedDepartment != null) {
            onDepartmentChange();
        }
    }
}