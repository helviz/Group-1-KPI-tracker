package org.pahappa.systems.kpiTracker.views.goalMgt;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.models.goalMgt.DepartmentGoal;
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

@ManagedBean(name = "departmentGoalView")
@Getter
@Setter
@ViewScoped
@ViewPath(path = HyperLinks.DEPARTMENT_GOAL_VIEW)
public class DepartmentGoalView
        extends PaginatedTableView<DepartmentGoal, DepartmentGoalService, DepartmentGoalService> {

    private DepartmentGoalService departmentGoalService;

    // Data
    private List<DepartmentGoal> departmentGoals;
    private DepartmentGoal selectedGoal;

    // Dashboard Metrics
    private DepartmentGoalService.DashboardMetrics dashboardMetrics;

    @PostConstruct
    public void init() {
        departmentGoalService = ApplicationContextProvider.getBean(DepartmentGoalService.class);
        this.reloadFilterReset();
        loadDashboardMetrics();
        loadDepartmentGoals();
    }

    @Override
    public void reloadFromDB(int i, int i1, Map<String, Object> map) throws Exception {
        Search search = new Search();
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        // Add fetch joins to eagerly load related entities
        search.addFetch("parentGoal");
        search.addFetch("owner");

        super.setDataModels(departmentGoalService.getInstances(search, i, i1));
    }

    @Override
    public List<ExcelReport> getExcelReportModels() {
        return null;
    }

    @Override
    public String getFileName() {
        return null;
    }

    public void loadDashboardMetrics() {
        try {
            dashboardMetrics = departmentGoalService.getDashboardMetrics();
            // Debug logging to help identify issues
            if (dashboardMetrics != null) {
                System.out.println("Dashboard Metrics Loaded - Total: " + dashboardMetrics.getTotalGoals() +
                        ", Active: " + dashboardMetrics.getActiveGoals() +
                        ", Completed: " + dashboardMetrics.getCompletedGoals() +
                        ", Overdue: " + dashboardMetrics.getOverdueGoals());
            } else {
                System.out.println("Dashboard Metrics is null!");
            }
        } catch (Exception e) {
            addErrorMessage("Error loading dashboard metrics: " + e.getMessage());
            e.printStackTrace(); // Add stack trace for debugging
        }
    }

    public void loadDepartmentGoals() {
        try {
            // Use the paginated data models which should have proper session management
            departmentGoals = getDataModels();
        } catch (Exception e) {
            addErrorMessage("Error loading department goals: " + e.getMessage());
            departmentGoals = new ArrayList<>();
        }
    }

    @Override
    public void reloadFilterReset() {
        Search countSearch = new Search();
        countSearch.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        super.setTotalRecords(departmentGoalService.countInstances(countSearch));
        try {
            super.reloadFilterReset();
            // Reload dashboard metrics when data is refreshed
            loadDashboardMetrics();
        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", e.getLocalizedMessage());
        }
    }

    /**
     * Deletes the specific GoalPeriod passed from the UI.
     *
     * @param selectedDepartmentGoal The record selected by the user in the data
     *                               table.
     */
    public void deleteDepartmentGoal(DepartmentGoal selectedDepartmentGoal) {
        try {
            // We check the parameter directly. It's much safer.
            if (selectedDepartmentGoal != null) {
                departmentGoalService.deleteInstance(selectedDepartmentGoal);
                MessageComposer.info("Success",
                        "Goal '" + selectedDepartmentGoal.getTitle() + "' has been deleted.");
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
    public List<DepartmentGoal> load(int i, int i1, Map<String, SortMeta> map, Map<String, FilterMeta> map1) {
        return getDataModels();
    }
}
