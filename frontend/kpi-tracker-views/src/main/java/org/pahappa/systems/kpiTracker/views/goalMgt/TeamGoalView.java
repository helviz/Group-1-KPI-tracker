package org.pahappa.systems.kpiTracker.views.goalMgt;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.TeamGoalService;
import org.pahappa.systems.kpiTracker.models.goalMgt.TeamGoal;
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

@ManagedBean(name = "teamGoalView")
@Getter
@Setter
@ViewScoped
@ViewPath(path = HyperLinks.TEAM_GOAL_VIEW)
public class TeamGoalView {

    private TeamGoalService teamGoalService;

    // Data
    private List<TeamGoal> teamGoals;
    private TeamGoal selectedGoal;

    // UI State
    private boolean showDeleteDialog = false;

    // Search and Filter
    private String searchTitle = "";
    private String searchTeam = "";
    private String searchOwner = "";
    private RecordStatus searchStatus = null;

    // Dashboard Metrics
    private TeamGoalService.DashboardMetrics dashboardMetrics;

    @PostConstruct
    public void init() {
        teamGoalService = ApplicationContextProvider.getBean(TeamGoalService.class);
        loadData();
        loadDashboardMetrics();
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

    public void searchGoals() {
        try {
            if (searchTitle.isEmpty() && searchTeam.isEmpty() &&
                    searchOwner.isEmpty() && searchStatus == null) {
                teamGoals = teamGoalService.findAllActive();
            } else {
                // For now, use findAllActive as placeholder
                // TODO: Implement proper search method in service
                teamGoals = teamGoalService.findAllActive();
            }
        } catch (Exception e) {
            addErrorMessage("Error searching goals: " + e.getMessage());
        }
    }

    public void clearSearch() {
        searchTitle = "";
        searchTeam = "";
        searchOwner = "";
        searchStatus = null;
        loadData();
    }

    public void showDeleteGoalDialog(TeamGoal goal) {
        this.selectedGoal = goal;
        this.showDeleteDialog = true;
    }

    public void deleteGoal() {
        try {
            if (selectedGoal != null) {
                teamGoalService.deleteInstance(selectedGoal);
                MessageComposer.info("Success", "Team goal '" + selectedGoal.getTitle() + "' has been deleted.");
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
