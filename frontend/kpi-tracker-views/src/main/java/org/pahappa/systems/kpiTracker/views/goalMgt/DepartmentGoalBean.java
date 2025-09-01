package org.pahappa.systems.kpiTracker.views.goalMgt;

import org.pahappa.systems.kpiTracker.core.services.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.core.services.OrganisationGoalService;
import org.pahappa.systems.kpiTracker.models.goalMgt.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.OrganisationGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.GoalLevel;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.User;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "departmentGoalBean")
@ViewScoped
public class DepartmentGoalBean {

    @javax.annotation.Resource
    private DepartmentGoalService departmentGoalService;

    @javax.annotation.Resource
    private OrganisationGoalService organisationGoalService;

    // Data
    private List<DepartmentGoal> departmentGoals;
    private List<OrganisationGoal> organisationGoals;
    private DepartmentGoal selectedGoal;
    private DepartmentGoal newGoal;

    // UI State
    private boolean showCreateDialog = false;
    private boolean showEditDialog = false;
    private boolean showDeleteDialog = false;

    // Search and Filter
    private String searchTitle = "";
    private String searchDepartment = "";
    private String searchOwner = "";
    private RecordStatus searchStatus = null;

    // Dashboard Metrics
    private DepartmentGoalService.DashboardMetrics dashboardMetrics;

    @javax.annotation.PostConstruct
    public void init() {
        loadData();
        loadDashboardMetrics();
    }

    public void loadData() {
        try {
            departmentGoals = departmentGoalService.findAllActive();
            organisationGoals = organisationGoalService.findAllActive();
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
                // Implement search logic based on filters
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
        searchGoals();
    }

    public void showCreateGoalDialog() {
        newGoal = new DepartmentGoal();
        newGoal.setGoalLevel(GoalLevel.DEPARTMENT);
        newGoal.setProgress(new java.math.BigDecimal("0.0"));
        newGoal.setContributionToParent(new java.math.BigDecimal("0.0"));
        newGoal.setEvaluationTarget(new java.math.BigDecimal("100.0"));
        newGoal.setIsActive(true);
        showCreateDialog = true;
    }

    public void createGoal() {
        try {
            if (newGoal.getParentGoal() == null) {
                addErrorMessage("Please select a parent organisation goal");
                return;
            }

            DepartmentGoal createdGoal = departmentGoalService.createDepartmentGoal(newGoal);
            addSuccessMessage("Department goal created successfully");

            showCreateDialog = false;
            loadData();
            loadDashboardMetrics();
        } catch (ValidationFailedException e) {
            addErrorMessage("Validation error: " + e.getMessage());
        } catch (Exception e) {
            addErrorMessage("Error creating goal: " + e.getMessage());
        }
    }

    public void showEditGoalDialog(DepartmentGoal goal) {
        selectedGoal = goal;
        showEditDialog = true;
    }

    public void updateGoal() {
        try {
            departmentGoalService.saveInstance(selectedGoal);
            addSuccessMessage("Department goal updated successfully");

            showEditDialog = false;
            loadData();
            loadDashboardMetrics();
        } catch (Exception e) {
            addErrorMessage("Error updating goal: " + e.getMessage());
        }
    }

    public void showDeleteGoalDialog(DepartmentGoal goal) {
        selectedGoal = goal;
        showDeleteDialog = true;
    }

    public void deleteGoal() {
        try {
            if (selectedGoal.getTeamGoals() != null && !selectedGoal.getTeamGoals().isEmpty()) {
                addErrorMessage("Cannot delete department goal with existing team goals");
                return;
            }

            departmentGoalService.deleteInstance(selectedGoal);
            addSuccessMessage("Department goal deleted successfully");

            showDeleteDialog = false;
            loadData();
            loadDashboardMetrics();
        } catch (Exception e) {
            addErrorMessage("Error deleting goal: " + e.getMessage());
        }
    }

    public void updateProgress(DepartmentGoal goal, double newProgress) {
        try {
            departmentGoalService.updateProgress(goal.getId(), newProgress);
            addSuccessMessage("Progress updated successfully");
            loadData();
            loadDashboardMetrics();
        } catch (Exception e) {
            addErrorMessage("Error updating progress: " + e.getMessage());
        }
    }

    private void addSuccessMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", message));
    }

    private void addErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", message));
    }

    // Getters and Setters
    public List<DepartmentGoal> getDepartmentGoals() {
        return departmentGoals;
    }

    public void setDepartmentGoals(List<DepartmentGoal> departmentGoals) {
        this.departmentGoals = departmentGoals;
    }

    public List<OrganisationGoal> getOrganisationGoals() {
        return organisationGoals;
    }

    public void setOrganisationGoals(List<OrganisationGoal> organisationGoals) {
        this.organisationGoals = organisationGoals;
    }

    public DepartmentGoal getSelectedGoal() {
        return selectedGoal;
    }

    public void setSelectedGoal(DepartmentGoal selectedGoal) {
        this.selectedGoal = selectedGoal;
    }

    public DepartmentGoal getNewGoal() {
        return newGoal;
    }

    public void setNewGoal(DepartmentGoal newGoal) {
        this.newGoal = newGoal;
    }

    public boolean isShowCreateDialog() {
        return showCreateDialog;
    }

    public void setShowCreateDialog(boolean showCreateDialog) {
        this.showCreateDialog = showCreateDialog;
    }

    public boolean isShowEditDialog() {
        return showEditDialog;
    }

    public void setShowEditDialog(boolean showEditDialog) {
        this.showEditDialog = showEditDialog;
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
