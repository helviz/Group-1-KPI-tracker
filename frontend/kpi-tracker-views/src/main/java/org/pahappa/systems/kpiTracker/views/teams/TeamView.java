package org.pahappa.systems.kpiTracker.views.teams;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.models.team.Team;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.dialogs.MessageComposer;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "teamView")
@Getter
@Setter
@ViewScoped
@ViewPath(path = HyperLinks.TEAMS_VIEW)
public class TeamView extends PaginatedTableView<Team, TeamService, TeamService> {
    public TeamService teamService;
    private DepartmentService departmentService;
    private Team selectedTeam;
    private String selectedDepartmentId; // To store the department ID for filtering

    @PostConstruct
    public void init() {
        this.teamService = ApplicationContextProvider.getBean(TeamService.class);
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);

        // Check if we have a department ID from flash scope (navigation from department
        // view)
        javax.faces.context.FacesContext facesContext = javax.faces.context.FacesContext.getCurrentInstance();
        if (facesContext != null) {
            Object flashDepartmentId = facesContext.getExternalContext().getFlash().get("selectedDepartmentId");
            if (flashDepartmentId != null) {
                this.selectedDepartmentId = flashDepartmentId.toString();
            }
        }

        this.reloadFilterReset();
    }

    @Override
    public void reloadFromDB(int first, int pageSize, Map<String, Object> filterBy) throws Exception {
        Search search = new Search();
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);

        // If we have a selected department ID, filter teams by that department
        if (selectedDepartmentId != null && !selectedDepartmentId.trim().isEmpty()) {
            search.addFilterEqual("department.id", selectedDepartmentId);
        }

        super.setDataModels(teamService.getInstances(search, first, pageSize));
    }

    @Override
    public List<Team> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        return getDataModels();
    }

    @Override
    public void reloadFilterReset() {
        Search search = new Search();

        // If we have a selected department ID, filter teams by that department
        if (selectedDepartmentId != null && !selectedDepartmentId.trim().isEmpty()) {
            search.addFilterEqual("department.id", selectedDepartmentId);
        }

        super.setTotalRecords(teamService.countInstances(search));
        try {
            super.reloadFilterReset();
        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", e.getLocalizedMessage());
        }
    }

    public void deleteSelectedTeam() {
        try {
            if (this.selectedTeam != null) {
                teamService.deleteInstance(this.selectedTeam);
                MessageComposer.info("Success", "Team '" + this.selectedTeam.getTeamName() + "' has been deleted.");
                this.selectedTeam = null;
                this.reloadFilterReset();
            } else {
                MessageComposer.error("Error", "No team was selected for deletion.");
            }
        } catch (Exception e) {
            MessageComposer.error("Deletion Failed", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<ExcelReport> getExcelReportModels() {
        return null;
    }

    @Override
    public String getFileName() {
        return "teams_report";
    }

    /**
     * Get the selected department name for display (if filtering by department)
     */
    public String getSelectedDepartmentName() {
        if (selectedDepartmentId != null && !selectedDepartmentId.trim().isEmpty()) {
            try {
                Department department = departmentService.getDepartmentById(selectedDepartmentId);
                if (department != null) {
                    return department.getName();
                }
                return "Unknown Department";
            } catch (Exception e) {
                return "Unknown Department";
            }
        }
        return null;
    }

    /**
     * Clear the department filter to show all teams
     */
    public void clearDepartmentFilter() {
        this.selectedDepartmentId = null;
        try {
            this.reloadFilterReset();
        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", e.getLocalizedMessage());
        }
    }
}