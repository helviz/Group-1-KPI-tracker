package org.pahappa.systems.kpiTracker.views.goalMgt;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.GoalService;
import org.pahappa.systems.kpiTracker.models.goalMgt.Goal;
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
    private transient GoalService goalService;
    private User loggedInUser;
    private String activeTabFilter = "MY_GOALS";

    // GoalForm is no longer needed in this bean, but we'll leave it in case of other uses.
    // @ManagedProperty(value = "#{goalForm}")
    // private GoalForm goalForm;

    @PostConstruct
    public void init() {
        this.goalService = ApplicationContextProvider.getBean(GoalService.class);
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
        List<Goal> goals = goalService.getGoalsByUserContext(this.activeTabFilter, this.loggedInUser.getId(), first, pageSize);
        super.setDataModels(goals);
    }

    @Override
    public void reloadFilterReset() throws Exception {
        if (this.loggedInUser == null) {
            super.setTotalRecords(0);
            return;
        }
        super.setTotalRecords(goalService.countGoalsByUserContext(this.activeTabFilter, this.loggedInUser.getId()));
        super.reloadFilterReset();
    }

    public void handleTabChange(TabChangeEvent event) throws Exception {
        String tabTitle = event.getTab().getTitle();
        switch (tabTitle) {
            case "My Goals": this.activeTabFilter = "MY_GOALS"; break;
            case "My Team": this.activeTabFilter = "MY_TEAM"; break;
            case "My Department": this.activeTabFilter = "MY_DEPARTMENT"; break;
            case "Organization": this.activeTabFilter = "ORGANIZATION"; break;
        }
        reloadFilterReset();
    }

    public String updateProgress(Goal goal) {
        if (goal.isIndividualGoal()) {
            return "/pages/goals/UpdateProgress.xhtml?id=" + goal.getId() + "&faces-redirect=true";
        }
        return null;
    }

    // --- The prepare... methods have been removed as they are now called directly from the UI ---

    @Override
    public List<ExcelReport> getExcelReportModels() { return Collections.emptyList(); }
    @Override
    public String getFileName() { return ""; }
    @Override
    public List<Goal> load(int i, int i1, Map<String, SortMeta> map, Map<String, FilterMeta> map1) {
        return super.getDataModels(); // Correct implementation for PrimeFaces lazy loading
    }
}