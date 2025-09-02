package org.pahappa.systems.kpiTracker.views.kpis;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.KpiService;
import org.pahappa.systems.kpiTracker.models.goalMgt.Goal;
import org.pahappa.systems.kpiTracker.models.kpi.KPI;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.MessageComposer;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "kpiView")
@Getter
@Setter
@SessionScoped
@ViewPath(path = HyperLinks.KPIS_VIEW)
public class KpiView extends PaginatedTableView<KPI, KpiView, KpiService> implements Serializable {

    private static final long serialVersionUID = 1L;
    private KpiService kpiService;
    private String searchTerm;
    private Goal selectedGoal;

    @ManagedProperty("#{kpiFormDialog}")
    private KpiFormDialog kpiFormDialog;

    @PostConstruct
    public void init() {
        this.kpiService = ApplicationContextProvider.getBean(KpiService.class);
        this.reloadFilterReset();
    }

    @Override
    public void reloadFromDB(int first, int pageSize, Map<String, Object> filterBy) throws Exception {
        Search search = createSearchQuery();
        super.setDataModels(kpiService.getInstances(search, first, pageSize));
        super.setTotalRecords(kpiService.countInstances(search));
    }

    @Override
    public List<KPI> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        return super.getDataModels();
    }

    @Override
    public void reloadFilterReset() {
        Search search = createSearchQuery();
        super.setTotalRecords(this.kpiService.countInstances(search));
        try {
            super.reloadFilterReset();
        } catch (Exception e) {
            MessageComposer.error("Reload Failed", e.getLocalizedMessage());
        }
    }

    private Search createSearchQuery() {
        Search search = new Search().addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        if (this.selectedGoal != null) {
            search.addFilterEqual("goal", this.selectedGoal);
        }

        if (searchTerm != null && !searchTerm.isEmpty()) {
            search.addFilterOr(
                    Filter.like("title", "%" + searchTerm + "%"),
                    Filter.like("description", "%" + searchTerm + "%")
            );
        }
        return search;
    }

    public void addKpi() {
        if (this.selectedGoal != null) {
            this.kpiFormDialog.reset(this.selectedGoal);
            this.kpiFormDialog.show(null);
        } else {
            MessageComposer.error("Error", "Please select a goal first.");
        }
    }

    public void editKpi(KPI kpi) {
        this.kpiFormDialog.setModel(kpi);
        this.kpiFormDialog.setFormProperties();
    }

    public void deleteKpi(KPI kpi) {
        try {
            this.kpiService.deleteInstance(kpi);
            MessageComposer.info("Success", "KPI '" + kpi.getTitle() + "' has been deleted.");
            this.reloadFilterReset();
        } catch (OperationFailedException e) {
            MessageComposer.error("Deletion Failed", e.getMessage());
            e.printStackTrace();
        }
    }

    public void onGoalSelected(Goal goal) {
        this.selectedGoal = goal;
        this.searchTerm = "";
        reloadFilterReset();
    }

    @Override
    public List<ExcelReport> getExcelReportModels() {
        return null;
    }

    @Override
    public String getFileName() {
        return "kpis_report";
    }
}