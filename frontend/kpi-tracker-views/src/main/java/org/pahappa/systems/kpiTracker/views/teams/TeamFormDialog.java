package org.pahappa.systems.kpiTracker.views.teams;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.models.team.Team;
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
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;

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
    private List<Department> availableDepartments;
    private List<User> availableUsers;
    private List<User> selectedMembers = new ArrayList<>();
    private boolean edit;

    @PostConstruct
    public void init() {
        try {
            this.teamService = ApplicationContextProvider.getBean(TeamService.class);
            this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
            this.userService = ApplicationContextProvider.getBean(UserService.class);

            this.availableDepartments = this.departmentService.getAllInstances();
            this.availableUsers = this.userService.getUsers();
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
        // Set members to the team model before persisting
        super.model.setMembers(new HashSet<>(selectedMembers));
        this.teamService.saveInstance(super.model);
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new Team();
        this.selectedMembers = new ArrayList<>();
        setEdit(false);
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        if (super.model != null && super.model.getId() != null) {
            setEdit(true);
            // Initialize selected members from the model for editing
            this.selectedMembers = new ArrayList<>(super.model.getMembers());
        } else {
            if (super.model == null) {
                super.model = new Team();
            }
            setEdit(false);
            this.selectedMembers = new ArrayList<>();
        }
    }

}