package org.pahappa.systems.kpiTracker.views.goalMgt;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.TeamGoalService;
import org.pahappa.systems.kpiTracker.models.goalMgt.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.TeamGoal;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "teamGoalView")
@Getter
@Setter
@ViewScoped
@ViewPath(path = HyperLinks.TEAM_GOAL_VIEW)
public class TeamGoalView  extends PaginatedTableView<TeamGoal,TeamGoalService,TeamGoalService> {

    private TeamGoalService teamGoalService;

    // Data
    private List<TeamGoal> teamGoals;
    private TeamGoal selectedTeamGoal;

    // Dashboard Metrics
    private TeamGoalService.DashboardMetrics dashboardMetrics;

    @PostConstruct
    public void init() {
        teamGoalService = ApplicationContextProvider.getBean(TeamGoalService.class);
       this.reloadFilterReset();
        loadData();
        loadDashboardMetrics();
    }

    @Override
    public void reloadFilterReset() {
        Search countSearch = new Search();
        countSearch.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        super.setTotalRecords(teamGoalService.countInstances(countSearch));
        try {
            super.reloadFilterReset();
            // Reload dashboard metrics when data is refreshed
            loadDashboardMetrics();
        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", e.getLocalizedMessage());
        }
    }
    @Override
    public void reloadFromDB(int i, int i1, Map<String, Object> map) throws Exception {
        Search search = new Search();
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        // Add fetch joins to eagerly load related entities
        search.addFetch("parentGoal");
        search.addFetch("owner");
     }

    @Override
    public List<ExcelReport> getExcelReportModels() {
        return null;
    }

    @Override
    public String getFileName() {
        return "";
    }

    public void loadData() {
        try {
            teamGoals = teamGoalService.findAllActive();
        } catch (Exception e) {
            addErrorMessage("Error loading data: " + e.getMessage());
        }
    }

    public void loadDashboardMetrics() {
        try {
            dashboardMetrics = teamGoalService.getDashboardMetrics();
        } catch (Exception e) {
            addErrorMessage("Error loading dashboard metrics: " + e.getMessage());
        }
    }

    public void deleteTeamGoal(TeamGoal selectedTeamGoal) {
        try {
            // We check the parameter directly. It's much safer.
            if (selectedTeamGoal != null) {
                teamGoalService.deleteInstance(selectedTeamGoal);
                MessageComposer.info("Success",
                        "Goal '" + selectedTeamGoal.getTitle() + "' has been deleted.");
                this.reloadFilterReset();
                // The table refresh will be handled by the 'update' attribute on the button.
            } else {
                // This is a safeguard. It should not happen if the UI is correct.
                MessageComposer.error("Error", "System Error: The record to delete was not provided.");
            }
        } catch (Exception e) {
            MessageComposer.error("Deletion Failed", e.getMessage());
            e.printStackTrace();
        }
    }




    public List<RecordStatus> getStatusOptions() {
        List<RecordStatus> options = new ArrayList<>();
        options.add(RecordStatus.ACTIVE);
        options.add(RecordStatus.DELETED);
        return options;
    }

    private void addErrorMessage(String message) {
        MessageComposer.error("Error", message);
    }

    @Override
    public List<TeamGoal> load(int i, int i1, Map<String, SortMeta> map, Map<String, FilterMeta> map1) {
        return getDataModels();
    }
}
