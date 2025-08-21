package org.pahappa.systems.kpiTracker.views.goalLevel;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.GoalLevelService;
import org.pahappa.systems.kpiTracker.models.goalMgt.GoalLevel;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.dialogs.MessageComposer;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.utils.SearchField;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@ViewPath(path = HyperLinks.GOAL_LEVEL_VIEW)
@Setter
@ManagedBean(name = "goalLevelView")
@ViewScoped
public class GoalLevelView extends PaginatedTableView<GoalLevel, GoalLevelService, GoalLevelService> {

    public GoalLevelService  goalLevelService;
    private GoalLevel selectedGoalLevel;
    private List<SearchField> searchFields, selectedSearchFields;


    @PostConstruct
    public void init() {
        try {
            goalLevelService = ApplicationContextProvider.getBean(GoalLevelService.class);

            this.reloadFilterReset();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<GoalLevel> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        return getDataModels();
    }

    @Override
    public void reloadFilterReset() {
        super.setTotalRecords(goalLevelService.countInstances(new Search()));
        try {
            super.reloadFilterReset();
        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", e.getLocalizedMessage());
        }

    }

    /**
     * Deletes the specific GoalPeriod passed from the UI.
     * @param selectedGoalLevel The record selected by the user in the data table.
     */
    public void deleteSelectedGoalLevel(GoalLevel selectedGoalLevel) {
        try {
            // We check the parameter directly. It's much safer.
            if (selectedGoalLevel != null) {
                goalLevelService.deleteInstance(selectedGoalLevel);
                MessageComposer.info("Success",
                        "Period '" + selectedGoalLevel.getName() + "' has been deleted.");
                // The table refresh will be handled by the 'update' attribute on the button.

            } else {
                // This is a safeguard. It should not happen if the UI is correct.
                MessageComposer.error("Error", "System Error: The record to delete was not provided.");
            }
        } catch (Exception e) {
            MessageComposer.error("Deletion Failed", e.getMessage());
            e.printStackTrace();
        }
    }
    @Override
    public void reloadFromDB(int i, int i1, Map<String, Object> map) throws Exception {
        super.setDataModels(  goalLevelService.getInstances(new Search().addFilterEqual("recordStatus", RecordStatus.ACTIVE), i, i1));
    }

    @Override
    public List<ExcelReport> getExcelReportModels() {
        return Collections.emptyList();
    }

    @Override
    public String getFileName() {
        return "";
    }

//    @Override
//    public List<GoalLevel> load(int i, int i1, Map<String, SortMeta> map, Map<String, FilterMeta> map1) {
//        return Collections.emptyList();
//    }
}
