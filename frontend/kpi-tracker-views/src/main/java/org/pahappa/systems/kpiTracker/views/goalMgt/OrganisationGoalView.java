package org.pahappa.systems.kpiTracker.views.goalMgt;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.OrganisationGoalService;
import org.pahappa.systems.kpiTracker.models.goalMgt.OrganisationGoal;
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

@ManagedBean(name = "organisationGoalView")
@Getter
@Setter
@ViewScoped
@ViewPath(path = HyperLinks.ORGANISATION_GOAL_VIEW)
public class OrganisationGoalView {

    private OrganisationGoalService organisationGoalService;

    // Data
    private List<OrganisationGoal> organisationGoals;
    private OrganisationGoal selectedGoal;

    // UI State
    private boolean showDeleteDialog = false;

    // Search and Filter
    private String searchTitle = "";
    private String searchOwner = "";
    private RecordStatus searchStatus = null;

    // Dashboard Metrics
    private OrganisationGoalService.OrganisationGoalMetrics dashboardMetrics;

    @PostConstruct
    public void init() {
        organisationGoalService = ApplicationContextProvider.getBean(OrganisationGoalService.class);
        loadData();
        loadDashboardMetrics();
    }

    public void loadData() {
        try {
            organisationGoals = organisationGoalService.findAllActive();
        } catch (Exception e) {
            addErrorMessage("Error loading data: " + e.getMessage());
        }
    }

    public void loadDashboardMetrics() {
        try {
            dashboardMetrics = organisationGoalService.getDashboardMetrics();
        } catch (Exception e) {
            addErrorMessage("Error loading dashboard metrics: " + e.getMessage());
        }
    }

    public void searchGoals() {
        try {
            // Implement search logic based on filters
            if (searchTitle.isEmpty() && searchOwner.isEmpty() && searchStatus == null) {
                organisationGoals = organisationGoalService.findAllActive();
            } else {
                // For now, use findAllActive as placeholder
                // TODO: Implement proper search method in service
                organisationGoals = organisationGoalService.findAllActive();
            }
        } catch (Exception e) {
            addErrorMessage("Error searching goals: " + e.getMessage());
        }
    }

    public void clearSearch() {
        searchTitle = "";
        searchOwner = "";
        searchStatus = null;
        loadData();
    }

    public void showDeleteGoalDialog(OrganisationGoal goal) {
        this.selectedGoal = goal;
        this.showDeleteDialog = true;
    }

    public void deleteGoal() {
        try {
            if (selectedGoal != null) {
                organisationGoalService.deleteInstance(selectedGoal);
                MessageComposer.info("Success",
                        "Organisation goal '" + selectedGoal.getTitle() + "' has been deleted.");
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


}
