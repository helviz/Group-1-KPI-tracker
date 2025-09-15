package org.pahappa.systems.kpiTracker.views.teams;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.StaffService;
import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.models.team.Team;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@ManagedBean(name = "teamFormDialog", eager = true)
@Getter
@Setter
@ViewPath(path = HyperLinks.TEAM_FORM_DIALOG)
@SessionScoped
public class TeamFormDialog extends DialogForm<Team> {

    private static final long serialVersionUID = 1L;
    private TeamService teamService;
    private DepartmentService departmentService;
    private StaffService staffService;
    private List<Department> availableDepartments;
    private Department selectedDepartment;
    private List<Staff> selectedStaff = new ArrayList<>();
    private List<Staff> availableStaffForSelect = new ArrayList<>();
    private boolean edit;
    private boolean departmentSelectionDisabled;

    @PostConstruct
    public void init() {
        try {
            this.teamService = ApplicationContextProvider.getBean(TeamService.class);
            this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
            this.staffService = ApplicationContextProvider.getBean(StaffService.class);
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
        super.getModel().setDepartment(selectedDepartment);
        Team savedTeam = this.teamService.saveInstance(super.getModel());

        // A more robust way is to calculate diffs, but clearing and re-adding is simpler for a form dialog.
        staffService.clearTeamMembers(savedTeam);
        if (this.selectedStaff != null && !this.selectedStaff.isEmpty()) {
            staffService.assignMultipleStaffToTeam(this.selectedStaff, savedTeam);
        }
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new Team();
        this.selectedStaff = new ArrayList<>();
        this.availableStaffForSelect = new ArrayList<>();
        this.selectedDepartment = null;
        this.departmentSelectionDisabled =false;
        setEdit(false);
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        if (super.model != null && super.model.getId() != null) {
            setEdit(true);
            this.selectedStaff = staffService.getStaffByTeam(getModel());
            this.selectedDepartment = getModel().getDepartment();
            this.departmentSelectionDisabled = true;
            onDepartmentChange();
        } else {
            if (super.model == null) {
                super.model = new Team();
            }
            setEdit(false);
            this.selectedStaff = new ArrayList<>();
            this.availableStaffForSelect = new ArrayList<>();
            this.selectedDepartment = null;
            this.departmentSelectionDisabled = false;
        }
    }

    public void onDepartmentChange() {
        if (this.selectedDepartment != null) {
            try {
                this.availableStaffForSelect = staffService.getStaffByDepartment(this.selectedDepartment);
            } catch (Exception e) {
                System.err.println("An error occurred while fetching users for department.");
                e.printStackTrace();
                this.availableStaffForSelect = new ArrayList<>();
            }
        } else {
            this.availableStaffForSelect = new ArrayList<>();
        }
    }

    public void prepareNew(Department department) {
        resetModal();
        if (super.model == null) {
            super.model = new Team();
        }
        getModel().setDepartment(department);
        this.selectedDepartment = department;
        this.departmentSelectionDisabled = true;
        if (this.selectedDepartment != null) {
            onDepartmentChange();
        }
    }
}