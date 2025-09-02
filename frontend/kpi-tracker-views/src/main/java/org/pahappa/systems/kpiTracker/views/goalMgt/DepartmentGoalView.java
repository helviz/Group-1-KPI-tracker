package org.pahappa.systems.kpiTracker.views.goalMgt;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.models.goalMgt.DepartmentGoal;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.MessageComposer;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "departmentGoalView")
@Getter
@Setter
@ViewScoped
@ViewPath(path = HyperLinks.DEPARTMENT_GOAL_VIEW)
public class DepartmentGoalView {

    private DepartmentGoalService departmentGoalService;

    // Data
    private List<DepartmentGoal> departmentGoals;
    private DepartmentGoal selectedGoal;

    // UI State
    private boolean showDeleteDialog = false;

    // Search and Filter
    private String searchTitle = "";
    private String searchDepartment = "";
    private String searchOwner = "";
    private RecordStatus searchStatus = null;

    // Dashboard Metrics
    private DepartmentGoalService.DashboardMetrics dashboardMetrics;

    @PostConstruct
    public void init() {
        departmentGoalService = ApplicationContextProvider.getBean(DepartmentGoalService.class);
        loadData();
        loadDashboardMetrics();
    }

    public void loadData() {
        try {
            departmentGoals = departmentGoalService.findAllActive();
        } catch (Exception e) {
            addErrorMessage("Error loading data: " + e.getMessage());
        }
    }

    public void loadDashboardMetrics() {
        try {
            dashboardMetrics = departmentGoalService.getDashboardMetrics();
        } catch (Exception e) {
            addErrorMessage("Error loading dashboard metrics: " + e.getMessage());
        }
    }

    public void searchGoals() {
        try {
            if (searchTitle.isEmpty() && searchDepartment.isEmpty() &&
                    searchOwner.isEmpty() && searchStatus == null) {
                departmentGoals = departmentGoalService.findAllActive();
            } else {
                // For now, use findAllActive as placeholder
                // TODO: Implement proper search method in service
                departmentGoals = departmentGoalService.findAllActive();
            }
        } catch (Exception e) {
            addErrorMessage("Error searching goals: " + e.getMessage());
        }
    }

    public void clearSearch() {
        searchTitle = "";
        searchDepartment = "";
        searchOwner = "";
        searchStatus = null;
        loadData();
    }

    public void showDeleteGoalDialog(DepartmentGoal goal) {
        this.selectedGoal = goal;
        this.showDeleteDialog = true;
    }

    public void deleteGoal() {
        try {
            if (selectedGoal != null) {
                departmentGoalService.deleteInstance(selectedGoal);
                MessageComposer.info("Success", "Department goal '" + selectedGoal.getTitle() + "' has been deleted.");
                this.showDeleteDialog = false;
                this.selectedGoal = null;
                loadData();
                loadDashboardMetrics();
            }
        } catch (Exception e) {
            MessageComposer.error("Deletion Failed", e.getMessage());
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

    // Getters and Setters
    public List<DepartmentGoal> getDepartmentGoals() {
        return departmentGoals;
    }

    public void setDepartmentGoals(List<DepartmentGoal> departmentGoals) {
        this.departmentGoals = departmentGoals;
    }

    public DepartmentGoal getSelectedGoal() {
        return selectedGoal;
    }

    public void setSelectedGoal(DepartmentGoal selectedGoal) {
        this.selectedGoal = selectedGoal;
    }

    public boolean isShowDeleteDialog() {
        return showDeleteDialog;
    }

    public void setShowDeleteDialog(boolean showDeleteDialog) {
        this.showDeleteDialog = showDeleteDialog;
    }

    public String getSearchTitle() {
        return searchTitle;
    }

    public void setSearchTitle(String searchTitle) {
        this.searchTitle = searchTitle;
    }

    public String getSearchDepartment() {
        return searchDepartment;
    }

    public void setSearchDepartment(String searchDepartment) {
        this.searchDepartment = searchDepartment;
    }

    public String getSearchOwner() {
        return searchOwner;
    }

    public void setSearchOwner(String searchOwner) {
        this.searchOwner = searchOwner;
    }

    public RecordStatus getSearchStatus() {
        return searchStatus;
    }

    public void setSearchStatus(RecordStatus searchStatus) {
        this.searchStatus = searchStatus;
    }

    public DepartmentGoalService.DashboardMetrics getDashboardMetrics() {
        return dashboardMetrics;
    }

    public void setDashboardMetrics(DepartmentGoalService.DashboardMetrics dashboardMetrics) {
        this.dashboardMetrics = dashboardMetrics;
    }
}
