package org.pahappa.systems.kpiTracker.views.goalMgt;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.IndividualGoalService;
import org.pahappa.systems.kpiTracker.core.services.TeamGoalService;
import org.pahappa.systems.kpiTracker.core.services.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.core.services.OrganisationGoalService;
import org.pahappa.systems.kpiTracker.models.goalMgt.IndividualGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.TeamGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.OrganisationGoal;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ManagedBean(name = "goalViews")
@ViewScoped
@ViewPath(path = HyperLinks.GOAL_VIEW)
public class GoalView extends PaginatedTableView<Object, Object, Object> {

    private static final long serialVersionUID = 1L;
    private transient IndividualGoalService individualGoalService;
    private transient TeamGoalService teamGoalService;
    private transient DepartmentGoalService departmentGoalService;
    private transient OrganisationGoalService organisationGoalService;
    private User loggedInUser;
    private String activeTabFilter = "MY_GOALS";

    // GoalForm is no longer needed in this bean, but we'll leave it in case of
    // other uses.
    // @ManagedProperty(value = "#{goalForm}")
    // private GoalForm goalForm;

    @PostConstruct
    public void init() {
        this.individualGoalService = ApplicationContextProvider.getBean(IndividualGoalService.class);
        this.teamGoalService = ApplicationContextProvider.getBean(TeamGoalService.class);
        this.departmentGoalService = ApplicationContextProvider.getBean(DepartmentGoalService.class);
        this.organisationGoalService = ApplicationContextProvider.getBean(OrganisationGoalService.class);
        this.loggedInUser = SharedAppData.getLoggedInUser();
        try {
            reloadFilterReset();
        } catch (Exception e) {
            // Consider logging the error
        }
    }

    @Override
    public void reloadFromDB(int first, int pageSize, Map<String, Object> filterBy) throws Exception {
        if (this.loggedInUser == null) {
            super.setDataModels(Collections.emptyList());
            return;
        }

        List<Object> goals = getGoalsByUserContext(this.activeTabFilter, this.loggedInUser.getId(), first, pageSize);
        super.setDataModels(goals);
    }

    @Override
    public void reloadFilterReset() throws Exception {
        if (this.loggedInUser == null) {
            super.setTotalRecords(0);
            return;
        }
        super.setTotalRecords((int) countGoalsByUserContext(this.activeTabFilter, this.loggedInUser.getId()));
        super.reloadFilterReset();
    }

    public void handleTabChange(TabChangeEvent event) throws Exception {
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
        reloadFilterReset();
    }

    /**
     * Get goals by user context using the new MBO goal services
     */
    private List<Object> getGoalsByUserContext(String context, String userId, int first, int pageSize) {
        try {
            switch (context) {
                case "MY_GOALS":
                    return new ArrayList<>(individualGoalService.findByOwner(userId));
                case "MY_TEAM":
                    return new ArrayList<>(teamGoalService.findAllActive());
                case "MY_DEPARTMENT":
                    return new ArrayList<>(departmentGoalService.findAllActive());
                case "ORGANIZATION":
                    return new ArrayList<>(organisationGoalService.findAllActive());
                default:
                    return Collections.emptyList();
            }
        } catch (Exception e) {
            // Log error and return empty list
            return Collections.emptyList();
        }
    }

    /**
     * Count goals by user context using the new MBO goal services
     */
    private long countGoalsByUserContext(String context, String userId) {
        try {
            switch (context) {
                case "MY_GOALS":
                    return individualGoalService.countActiveGoals();
                case "MY_TEAM":
                    return teamGoalService.countActiveGoals();
                case "MY_DEPARTMENT":
                    return departmentGoalService.countActiveGoals();
                case "ORGANIZATION":
                    return organisationGoalService.countActiveGoals();
                default:
                    return 0;
            }
        } catch (Exception e) {
            // Log error and return 0
            return 0;
        }
    }

    public String updateProgress(Object goal) {
        // Since we're now working with different goal types, we need to check the type
        if (goal instanceof IndividualGoal) {
            return "/pages/goals/UpdateProgress.xhtml?id=" + ((IndividualGoal) goal).getId() + "&faces-redirect=true";
        }
        return null;
    }

    // --- The prepare... methods have been removed as they are now called directly
    // from the UI ---

    @Override
    public List<ExcelReport> getExcelReportModels() {
        return Collections.emptyList();
    }

    @Override
    public String getFileName() {
        return "";
    }

    @Override
    public List<Object> load(int i, int i1, Map<String, SortMeta> map, Map<String, FilterMeta> map1) {
        return super.getDataModels(); // Correct implementation for PrimeFaces lazy loading
    }
}