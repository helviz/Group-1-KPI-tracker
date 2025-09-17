package org.pahappa.systems.kpiTracker.views.goal;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.constants.GoalLevel;
import org.pahappa.systems.kpiTracker.constants.GoalStatus;
import org.pahappa.systems.kpiTracker.core.services.GoalService;
import org.pahappa.systems.kpiTracker.core.services.StaffService;
import org.pahappa.systems.kpiTracker.models.goalCreation.Goal;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.MessageComposer;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@ViewPath(path = HyperLinks.GOAL_VIEW)
@ManagedBean(name = "goalView")
@ViewScoped
@Getter // Lombok to generate all getters
@Setter // Lombok to generate all setters
public class GoalView implements Serializable {
    private static final long serialVersionUID = 1L;
    private GoalService goalService;
    private StaffService staffService;
    private int activeTabIndex;

    // --- NEW PROPERTIES TO HOLD DATA FOR THE VIEW ---
    private List<Goal> myGoals;
    private List<Goal> organisationGoals;
    private List<Goal> departmentGoals;
    private List<Goal> teamGoals;
    private DashboardMetrics dashboardMetrics;

    @PostConstruct
    public void init() {
        this.goalService = ApplicationContextProvider.getBean(GoalService.class);
        this.staffService = ApplicationContextProvider.getBean(StaffService.class);
        this.dashboardMetrics = new DashboardMetrics(); // Initialize the metrics object
        reloadFilterReset();
    }

    public void reloadFilterReset() {
        reloadGoals();
        loadDashboardMetrics();
        this.activeTabIndex = 0; // Set the active tab to the first one
    }

    private void reloadGoals() {
        // Assign fetched lists to the class properties
        this.myGoals = fetchMyGoals();
        this.organisationGoals = fetchGoalsByLevel(GoalLevel.ORGANISATION);
        this.departmentGoals = fetchGoalsByLevel(GoalLevel.DEPARTMENT);
        this.teamGoals = fetchGoalsByLevel(GoalLevel.TEAM);
    }

    private List<Goal> fetchMyGoals() {
        User loggedInUser = SharedAppData.getLoggedInUser();
        System.out.println("Logged-in User: "
                + (loggedInUser != null ? loggedInUser.getFirstName() + " " + loggedInUser.getLastName() : "null"));
        if (loggedInUser == null) {
            MessageComposer.error("Error", "No logged-in user found. Please log in again.");
            return Collections.emptyList();
        }

        // Convert User to Staff object
        Staff staff = staffService.getStaffByUser(loggedInUser);
        if (staff == null) {
            MessageComposer.error("Error",
                    "No staff record found for the logged-in user. Please contact administrator.");
            return Collections.emptyList();
        }

        // Use the new method with JOIN FETCH to avoid LazyInitializationException
        List<Goal> goals = goalService.getGoalsByOwner(staff, GoalLevel.INDIVIDUAL);
        System.out.println("My Goals fetched: " + goals.size());
        return goals;
    }

    private List<Goal> fetchGoalsByLevel(GoalLevel level) {
        // Use the new method with JOIN FETCH to avoid LazyInitializationException
        List<Goal> goals = goalService.getGoalsByLevel(level);
        System.out.println(level.getDisplayName() + " Goals fetched: " + goals.size());
        return goals;
    }

    private void loadDashboardMetrics() {
        Search allSearch = new Search(Goal.class);
        allSearch.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        this.dashboardMetrics.setTotalGoals(goalService.countInstances(allSearch));

        // --- CORRECTED LOGIC ---
        // Use 'goalStatus' and 'goalPeriod.endDate' which are mapped to the database.

        // Active goals are those that are not completed and whose end date is in the
        // future.
        Search activeSearch = new Search(Goal.class);
        activeSearch.addFilterNotEqual("goalStatus", GoalStatus.COMPLETED); // Not completed yet
        activeSearch.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        activeSearch.addFilterOr(
                Filter.isNull("goalPeriod.endDate"), // No end date is considered active
                Filter.greaterThan("goalPeriod.endDate", new Date()) // End date is in the future
        );
        this.dashboardMetrics.setActiveGoals(goalService.countInstances(activeSearch));

        // Completed goals are straightforward.
        Search completedSearch = new Search(Goal.class);
        completedSearch.addFilterEqual("goalStatus", GoalStatus.COMPLETED);
        completedSearch.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        this.dashboardMetrics.setCompletedGoals(goalService.countInstances(completedSearch));

        // Overdue goals are not completed and their end date is in the past.
        Search overdueSearch = new Search(Goal.class);
        overdueSearch.addFilterNotEqual("goalStatus", GoalStatus.COMPLETED); // Not completed yet
        overdueSearch.addFilterNotNull("goalPeriod.endDate"); // Must have an end date
        overdueSearch.addFilterLessThan("goalPeriod.endDate", new Date()); // End date is in the past
        overdueSearch.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        this.dashboardMetrics.setOverdueGoals(goalService.countInstances(overdueSearch));
    }

    public void deleteSelectedGoal(Goal goalToDelete) {
        try {
            if (goalToDelete != null) {
                goalService.deleteInstance(goalToDelete);
                MessageComposer.info("Success", "Goal '" + goalToDelete.getGoalName() + "' has been deleted.");
                reloadGoals(); // Just reload goals, no need to reload everything
            } else {
                MessageComposer.error("Error", "System Error: The record to delete was not provided.");
            }
        } catch (Exception e) {
            MessageComposer.error("Deletion Failed", e.getMessage());
            e.printStackTrace();
        }
    }

    // Inner class for DashboardMetrics
    @Setter
    @Getter
    public static class DashboardMetrics {
        private int totalGoals;
        private int activeGoals;
        private int completedGoals;
        private int overdueGoals;
    }
}