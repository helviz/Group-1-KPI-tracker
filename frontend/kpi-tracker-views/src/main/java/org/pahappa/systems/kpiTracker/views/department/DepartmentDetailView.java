package org.pahappa.systems.kpiTracker.views.department;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.StaffService;
import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.models.team.Team;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "departmentDetailView")
@Getter
@Setter
@ViewScoped
public class DepartmentDetailView {

    private DepartmentService departmentService;
    private TeamService teamService;
    private StaffService staffService;

    private Department department;
    private List<Team> teams;
    private List<Staff> unassignedStaff;
    private String departmentId;

    @PostConstruct
    public void init() {
        try {
            this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
            this.teamService = ApplicationContextProvider.getBean(TeamService.class);
            this.staffService = ApplicationContextProvider.getBean(StaffService.class);

            // Get department ID from flash scope or request parameters
            javax.faces.context.FacesContext facesContext = javax.faces.context.FacesContext.getCurrentInstance();
            if (facesContext != null) {
                // Try to get from flash scope first (from department view navigation)
                Object flashDepartmentId = facesContext.getExternalContext().getFlash().get("selectedDepartmentId");
                if (flashDepartmentId != null) {
                    this.departmentId = flashDepartmentId.toString();
                } else {
                    // Try to get from request parameters
                    String paramDepartmentId = facesContext.getExternalContext().getRequestParameterMap()
                            .get("departmentId");
                    if (paramDepartmentId != null) {
                        this.departmentId = paramDepartmentId;
                    }
                }
            }

            if (this.departmentId != null && !this.departmentId.trim().isEmpty()) {
                loadDepartmentDetails();
            }

        } catch (Exception e) {
            System.err.println("Error initializing DepartmentDetailView: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load department details, teams, and unassigned members
     */
    private void loadDepartmentDetails() {
        try {
            // Load department
            this.department = departmentService.getDepartmentById(this.departmentId);
            if (this.department == null) {
                System.err.println("Department not found with ID: " + this.departmentId);
                return;
            }

            // Load teams for this department
            Search teamSearch = new Search();
            teamSearch.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
            teamSearch.addFilterEqual("department.id", this.departmentId);
            this.teams = teamService.getInstances(teamSearch, 0, 1000); // Get all teams

            // Load unassigned staff (staff in department but not in any team)
            this.unassignedStaff = staffService.getUnassignedStaffInDepartment(this.department);

        } catch (Exception e) {
            System.err.println("Error loading department details: " + e.getMessage());
            e.printStackTrace();
            this.teams = new ArrayList<>();
            this.unassignedStaff = new ArrayList<>();
        }
    }

    /**
     * Navigate back to departments list
     */
    public String navigateToDepartments() {
        return HyperLinks.DEPARTMENT_VIEW + "?faces-redirect=true";
    }

    /**
     * Get department icon based on department name or type
     */
    public String getDepartmentIcon() {
        if (department == null || department.getName() == null) {
            return "fa fa-building";
        }

        String name = department.getName().toLowerCase();
        if (name.contains("tech") || name.contains("it") || name.contains("software")) {
            return "fa fa-desktop";
        } else if (name.contains("hr") || name.contains("human")) {
            return "fa fa-users";
        } else if (name.contains("finance") || name.contains("account")) {
            return "fa fa-calculator";
        } else if (name.contains("marketing") || name.contains("sales")) {
            return "fa fa-bullhorn";
        } else if (name.contains("operations") || name.contains("admin")) {
            return "fa fa-cogs";
        } else {
            return "fa fa-building";
        }
    }

    /**
     * Get total member count for the department
     */
    public int getTotalMemberCount() {
        if (this.department == null) {
            return 0;
        }
        return staffService.getStaffByDepartment(this.department).size();
    }

    public void nothingStayOnPage(){

    }

    /**
     * Check if department has any teams
     */
    public boolean hasTeams() {
        return teams != null && !teams.isEmpty();
    }

    /**
     * Check if department has unassigned members
     */
    public boolean hasUnassignedStaff() {
        return unassignedStaff != null && !unassignedStaff.isEmpty();
    }

    public String navigateToTeamDetail(Team team) {
        if (team != null) {
            // Store the selected team in flash scope for the detail view
            javax.faces.context.FacesContext.getCurrentInstance()
                    .getExternalContext().getFlash().put("selectedTeamId", team.getId());
            return HyperLinks.TEAM_DETAIL_VIEW + "?faces-redirect=true";
        }
        return null;
    }
}
