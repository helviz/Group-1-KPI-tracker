package org.pahappa.systems.kpiTracker.views;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.sers.webutils.client.controllers.WebAppExceptionHandler;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.security.User;
import org.sers.webutils.model.utils.SortField;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "dashboard")
@ViewScoped
@ViewPath(path = HyperLinks.DASHBOARD)
public class Dashboard extends WebAppExceptionHandler implements Serializable {

    private static final long serialVersionUID = 1L;
    private User loggedinUser;

    Search search = new Search();
    private String searchTerm;
    private SortField selectedSortField;

    // Dashboard Metrics
    private Integer totalGoals;
    private Integer activeUsers;
    private Integer activeKpis;
    private Integer overdueGoals;
    private Integer achievedGoals;
    private Integer pendingApprovals;
    private Integer rewardsIssued;

    // Activity and Performance Data
    private List<Activity> recentActivities;
    private List<DepartmentPerformance> departmentPerformances;
    private String selectedPeriod = "quarterly";

    @SuppressWarnings("unused")
    private String viewPath;

    @PostConstruct
    public void init() {
        loggedinUser = SharedAppData.getLoggedInUser();
        loadDashboardData();
    }

    private void loadDashboardData() {
        // Load metrics - replace with actual data service calls
        loadMetrics();
        loadRecentActivities();
        loadDepartmentPerformances();
    }

    private void loadMetrics() {
        // TODO: Replace with actual service calls to get real data
        // For now, using sample data matching your image

        try {
            // Example: this.totalGoals = goalService.getTotalGoalsCount();
            this.totalGoals = 27;
            this.activeUsers = 27;
            this.activeKpis = 27;
            this.overdueGoals = 27;
            this.achievedGoals = 12;
            this.pendingApprovals = 27;
            this.rewardsIssued = 2;
        } catch (Exception e) {
        }
    }

    private void loadRecentActivities() {
        this.recentActivities = new ArrayList<>();

        try {
            // TODO: Replace with actual service call
            // Example: this.recentActivities = activityService.getRecentActivities(10);

            // Sample data for now
            recentActivities.add(new Activity("New Goal Created",
                    "Q4 Sales Target created by Semaganda",
                    "2 hours ago", "Semaganda"));

            recentActivities.add(new Activity("New Goal Created",
                    "Q4 Sales Target created by Semaganda",
                    "2 hours ago", "Semaganda"));

            recentActivities.add(new Activity("New Goal Created",
                    "Q4 Sales Target created by Semaganda",
                    "2 hours ago", "Semaganda"));

        } catch (Exception e) {

        }
    }

    private void loadDepartmentPerformances() {
        this.departmentPerformances = new ArrayList<>();

        try {
            // TODO: Replace with actual service call
            // Example: this.departmentPerformances = performanceService.getDepartmentPerformances(selectedPeriod);

            // Sample data matching your image
            departmentPerformances.add(new DepartmentPerformance("Tech", 92));
            departmentPerformances.add(new DepartmentPerformance("Sales", 42));
            departmentPerformances.add(new DepartmentPerformance("Partnership", 95));
            departmentPerformances.add(new DepartmentPerformance("HR", 32));

        } catch (Exception e) {

        }
    }


    // Method to refresh data when period changes
    public void onPeriodChange() {
        loadDepartmentPerformances();
    }

    // Getters and Setters
    public User getLoggedinUser() {
        return loggedinUser;
    }

    public void setLoggedinUser(User loggedinUser) {
        this.loggedinUser = loggedinUser;
    }

    public String getViewPath() {
        return Dashboard.class.getAnnotation(ViewPath.class).path();
    }

    public void setViewPath(String viewPath) {
        this.viewPath = viewPath;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public SortField getSelectedSortField() {
        return selectedSortField;
    }

    public void setSelectedSortField(SortField selectedSortField) {
        this.selectedSortField = selectedSortField;
    }

    // Dashboard Metrics Getters and Setters
    public Integer getTotalGoals() {
        return totalGoals;
    }

    public void setTotalGoals(Integer totalGoals) {
        this.totalGoals = totalGoals;
    }

    public Integer getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(Integer activeUsers) {
        this.activeUsers = activeUsers;
    }

    public Integer getActiveKpis() {
        return activeKpis;
    }

    public void setActiveKpis(Integer activeKpis) {
        this.activeKpis = activeKpis;
    }

    public Integer getOverdueGoals() {
        return overdueGoals;
    }

    public void setOverdueGoals(Integer overdueGoals) {
        this.overdueGoals = overdueGoals;
    }

    public Integer getAchievedGoals() {
        return achievedGoals;
    }

    public void setAchievedGoals(Integer achievedGoals) {
        this.achievedGoals = achievedGoals;
    }

    public Integer getPendingApprovals() {
        return pendingApprovals;
    }

    public void setPendingApprovals(Integer pendingApprovals) {
        this.pendingApprovals = pendingApprovals;
    }

    public Integer getRewardsIssued() {
        return rewardsIssued;
    }

    public void setRewardsIssued(Integer rewardsIssued) {
        this.rewardsIssued = rewardsIssued;
    }

    public List<Activity> getRecentActivities() {
        return recentActivities;
    }

    public void setRecentActivities(List<Activity> recentActivities) {
        this.recentActivities = recentActivities;
    }

    public List<DepartmentPerformance> getDepartmentPerformances() {
        return departmentPerformances;
    }

    public void setDepartmentPerformances(List<DepartmentPerformance> departmentPerformances) {
        this.departmentPerformances = departmentPerformances;
    }

    public String getSelectedPeriod() {
        return selectedPeriod;
    }

    public void setSelectedPeriod(String selectedPeriod) {
        this.selectedPeriod = selectedPeriod;
        onPeriodChange(); // Reload data when period changes
    }

    // Inner classes for data structures
    public static class Activity implements Serializable {
        private static final long serialVersionUID = 1L;

        private String title;
        private String description;
        private String timeAgo;
        private String user;

        public Activity() {}

        public Activity(String title, String description, String timeAgo, String user) {
            this.title = title;
            this.description = description;
            this.timeAgo = timeAgo;
            this.user = user;
        }

        // Getters and Setters
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTimeAgo() {
            return timeAgo;
        }

        public void setTimeAgo(String timeAgo) {
            this.timeAgo = timeAgo;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }
    }

    public static class DepartmentPerformance implements Serializable {
        private static final long serialVersionUID = 1L;

        private String name;
        private Integer percentage;

        public DepartmentPerformance() {}

        public DepartmentPerformance(String name, Integer percentage) {
            this.name = name;
            this.percentage = percentage;
        }

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getPercentage() {
            return percentage;
        }

        public void setPercentage(Integer percentage) {
            this.percentage = percentage;
        }
    }
}