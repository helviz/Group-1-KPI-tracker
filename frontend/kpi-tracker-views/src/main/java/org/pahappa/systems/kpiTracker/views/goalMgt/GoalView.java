package org.pahappa.systems.kpiTracker.views.goalMgt;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.constants.GoalStatus;
import org.pahappa.systems.kpiTracker.core.services.AssignedUserService;
import org.pahappa.systems.kpiTracker.core.services.GoalService;
import org.pahappa.systems.kpiTracker.models.goalMgt.Goal;
import org.pahappa.systems.kpiTracker.models.goalMgt.GoalPeriod;
import org.pahappa.systems.kpiTracker.models.user.AssignedUser;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ManagedBean(name = "goalView")
@ViewScoped
@ViewPath(path = HyperLinks.GOAL_VIEW)
public class GoalView extends PaginatedTableView<Goal, GoalService, GoalService> {

    private static final long serialVersionUID = 1L;

    // --- SERVICES ---
    private GoalService goalService;
    private AssignedUserService assignedUserService;

    // --- NEW PROPERTIES FOR FILTERING ---
    private User loggedInUser;
    private AssignedUser assignedUser;
    private String activeTabFilter = "MY_GOALS";

    // --- Context-specific goal lists ---
    private List<Goal> teamGoals;
    private List<Goal> departmentGoals;
    private List<Goal> organizationGoals;

    // --- Your existing properties ---
    private String searchTerm;
    private GoalStatus selectedStatus;
    private GoalPeriod selectedPeriod;
    private List<GoalStatus> availableStatuses = Arrays.asList(GoalStatus.values());
    private List<GoalPeriod> availablePeriods;
    private int totalCompleted, totalAtRisk, totalOnTrack, totalBehind;

    @PostConstruct
    public void init() {
        try {
            this.goalService = ApplicationContextProvider.getBean(GoalService.class);
            this.assignedUserService = ApplicationContextProvider.getBean(AssignedUserService.class);
            this.availablePeriods = ApplicationContextProvider
                    .getBean(org.pahappa.systems.kpiTracker.core.services.GoalPeriodService.class)
                    .getAllInstances();

            // Get the current user and their assignment profile
            this.loggedInUser = SharedAppData.getLoggedInUser();
            if (this.loggedInUser != null) {
                this.assignedUser = assignedUserService.findAssignedUserByUser(this.loggedInUser);
            }

            this.reloadFilterReset();
            this.loadAllContextGoals();
        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", e.getLocalizedMessage());
        }
    }

    /**
     * Called by the <p:tabView> component in the XHTML when a tab is clicked.
     */
    public void handleTabChange(TabChangeEvent event) {
        try {
            String tabTitle = event.getTab().getTitle();

            switch (tabTitle) {
                case "My Goals":
                    this.activeTabFilter = "MY_GOALS";
                    break;
                case "My Team":
                    this.activeTabFilter = "MY_TEAM";
                    break;
                case "My Department":
                    this.activeTabFilter = "MY_DEPARTMENT";
                    break;
                case "Organization":
                    this.activeTabFilter = "ORGANIZATION";
                    break;
            }

            this.reloadFilterReset();
        } catch (Exception e) {
            UiUtils.ComposeFailure("Tab Change Error", e.getLocalizedMessage());
        }
    }

    /**
     * Load goals for all contexts
     */
    private void loadAllContextGoals() {
        if (this.loggedInUser == null) {
            this.teamGoals = Collections.emptyList();
            this.departmentGoals = Collections.emptyList();
            this.organizationGoals = Collections.emptyList();
            return;
        }

        try {
            this.teamGoals = goalService.getGoalsByUserContext("MY_TEAM", this.loggedInUser.getId(), 0, 100);
            this.departmentGoals = goalService.getGoalsByUserContext("MY_DEPARTMENT", this.loggedInUser.getId(), 0,
                    100);
            this.organizationGoals = goalService.getGoalsByUserContext("ORGANIZATION", this.loggedInUser.getId(), 0,
                    100);
        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", "Failed to load context goals: " + e.getMessage());
            this.teamGoals = Collections.emptyList();
            this.departmentGoals = Collections.emptyList();
            this.organizationGoals = Collections.emptyList();
        }
    }

    @Override
    public void reloadFromDB(int first, int pageSize, Map<String, Object> filterBy) throws Exception {
        if (this.loggedInUser == null) {
            super.setDataModels(Collections.emptyList());
            return;
        }

        List<Goal> goals = goalService.getGoalsByUserContext(this.activeTabFilter, this.loggedInUser.getId(), first,
                pageSize);
        super.setDataModels(goals);
    }

    @Override
    public List<Goal> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        return super.getDataModels();
    }

    @Override
    public void reloadFilterReset() {
        if (this.loggedInUser == null) {
            super.setTotalRecords(0);
            return;
        }

        try {
            int totalCount = goalService.countGoalsByUserContext(this.activeTabFilter, this.loggedInUser.getId());
            super.setTotalRecords(totalCount);
            super.reloadFilterReset();
        } catch (Exception e) {
            UiUtils.ComposeFailure("Reload Failed", e.getLocalizedMessage());
        }
    }

    @Override
    public List<ExcelReport> getExcelReportModels() {
        return Collections.emptyList();
    }

    /**
     * Add new goal action
     */
    public void addNewGoal() {
        // This would typically open a dialog or navigate to the goal form
        // For now, we'll just show a message
        // UiUtils.ComposeSuccess("Info", "Add New Goal functionality will be
        // implemented");
    }

    /**
     * Edit goal action
     */
    public void editGoal(Goal goal) {
        // This would typically open the goal form dialog with the selected goal
        // UiUtils.ComposeSuccess("Info", "Edit Goal functionality will be implemented
        // for: " + goal.getGoalTitle());
    }

    /**
     * Update progress action for Individual goals.
     * This method now returns a String to handle navigation.
     */
    public String updateProgress(Goal goal) {
        try {
            if (goal.isIndividualGoal()) {
                // The returned string is the navigation outcome.
                // "?faces-redirect=true" is added for a clean URL update in the browser.
                return "/pages/goals/UpdateProgress.xhtml?id=" + goal.getId() + "&faces-redirect=true";
            } else {
                UiUtils.ComposeFailure("Error", "Progress can only be updated for Individual goals.");
                return null; // A null return tells JSF to stay on the current page.
            }
        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", e.getLocalizedMessage());
            return null; // Stay on the current page in case of an error.
        }
    }

    @Override
    public String getFileName() {
        return "goals_report";
    }
}