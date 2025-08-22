package org.pahappa.systems.kpiTracker.views.goalMgt;

import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.Filter;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.constants.GoalStatus;
import org.pahappa.systems.kpiTracker.core.services.GoalService;
import org.pahappa.systems.kpiTracker.models.goalMgt.Goal;
import org.pahappa.systems.kpiTracker.models.goalMgt.GoalPeriod;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
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

    private GoalService goalService;

    // Filters/search
    private String searchTerm;
    private GoalStatus selectedStatus;
    private GoalPeriod selectedPeriod; // optional UI hook
    private List<GoalStatus> availableStatuses = Arrays.asList(GoalStatus.values());
    private List<GoalPeriod> availablePeriods;

    // KPI counters for header cards
    private int totalCompleted;
    private int totalAtRisk;
    private int totalOnTrack;
    private int totalBehind;

    @PostConstruct
    public void init() {
        try {
            this.goalService = ApplicationContextProvider.getBean(GoalService.class);
            // load periods lazily via GenericService to populate dropdown if needed
            this.availablePeriods = ApplicationContextProvider
                    .getBean(org.pahappa.systems.kpiTracker.core.services.GoalPeriodService.class)
                    .getAllInstances();
            this.reloadFilterReset();
        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", e.getLocalizedMessage());
        }
    }

    @Override
    public void reloadFromDB(int offset, int limit, Map<String, Object> filters) throws Exception {
        super.setDataModels(goalService.getInstances(composeSearch(), offset, limit));
    }

    @Override
    public List<Goal> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        return getDataModels();
    }

    @Override
    public void reloadFilterReset() {
        try {
            Search search = composeSearch();
            super.setTotalRecords(goalService.countInstances(search));

            // KPI counters
            this.totalCompleted = goalService.countInstances(new Search()
                    .addFilterEqual("recordStatus", RecordStatus.ACTIVE)
                    .addFilterEqual("goalStatus", GoalStatus.COMPLETED));
            this.totalAtRisk = goalService.countInstances(new Search()
                    .addFilterEqual("recordStatus", RecordStatus.ACTIVE)
                    .addFilterEqual("goalStatus", GoalStatus.AT_RISK));
            this.totalOnTrack = goalService.countInstances(new Search()
                    .addFilterEqual("recordStatus", RecordStatus.ACTIVE)
                    .addFilterEqual("goalStatus", GoalStatus.ON_TRACK));
            this.totalBehind = goalService.countInstances(new Search()
                    .addFilterEqual("recordStatus", RecordStatus.ACTIVE)
                    .addFilterEqual("goalStatus", GoalStatus.BEHIND));

            super.reloadFilterReset();
        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", e.getLocalizedMessage());
        }
    }

    private Search composeSearch() {
        Search search = new Search()
                .addFilterEqual("recordStatus", RecordStatus.ACTIVE);

        // ====================================================================================
        // === THIS IS THE FIX: Eagerly fetch the goalPeriod to avoid
        // LazyInitializationException
        // on the JSF page when accessing g.goalPeriod.endDate.
        // ====================================================================================
        search.addFetch("goalPeriod");

        if (selectedStatus != null) {
            search.addFilterEqual("goalStatus", selectedStatus);
        }

        if (selectedPeriod != null) {
            search.addFilterEqual("goalPeriod", selectedPeriod);
        }

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            search.addFilterOr(
                    Filter.like("goalTitle", "%" + searchTerm + "%"),
                    Filter.like("description", "%" + searchTerm + "%"));
        }

        return search;
    }

    @Override
    public List<ExcelReport> getExcelReportModels() {
        return Collections.emptyList();
    }

    @Override
    public String getFileName() {
        return "goals";
    }
}