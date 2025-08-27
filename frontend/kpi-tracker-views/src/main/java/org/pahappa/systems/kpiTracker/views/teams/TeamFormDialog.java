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
// import java.util.stream.Collectors; // No longer needed

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
    private List<User> selectedMembers = new ArrayList<>();
    private List<User> availableUsersForSelect = new ArrayList<>();

    private boolean edit;

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
        // Set members to the team model from the selectedMembers list
        super.model.setMembers(new HashSet<User>(this.selectedMembers));
        this.teamService.saveInstance(super.model);
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new Team();
        this.selectedMembers = new ArrayList<User>();
        this.availableUsersForSelect = new ArrayList<User>();
        setEdit(false);
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        if (super.model != null && super.model.getId() != null) {
            setEdit(true);
            this.selectedMembers = new ArrayList<User>(super.model.getMembers());
            onDepartmentChange();
        } else {
            if (super.model == null) {
                super.model = new Team();
            }
            setEdit(false);
            this.selectedMembers = new ArrayList<User>();
            this.availableUsersForSelect = new ArrayList<User>();
        }
    }

    public void onDepartmentChange() {
        if (super.getModel().getDepartment() != null) {
            try {
                List<AssignedUser> assignedUsersInDepartment = assignedUserService.getAssignedUsersByDepartment(super.getModel().getDepartment());
                    // 1. Create a new empty list to hold the results
                List<User> users = new ArrayList<User>();

                // 2. Loop through each AssignedUser in the original list
                for (AssignedUser assignedUser : assignedUsersInDepartment) {
                    // 3. For each one, get the User and add it to our new list
                    users.add(assignedUser.getUser());
                }

                // 4. Assign the newly populated list
                this.availableUsersForSelect = users;

                // ===================== MODIFIED SECTION END =====================

            } catch (Exception e) {
                System.err.println("An error occurred while fetching users for department.");
                e.printStackTrace();
                this.availableUsersForSelect = new ArrayList<User>();
            }
        } else {
            this.availableUsersForSelect = new ArrayList<User>();
        }
    }
}