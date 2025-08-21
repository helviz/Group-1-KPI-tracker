package org.pahappa.systems.kpiTracker.views.OrganisationFit;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.SurveyCategoryService;
import org.pahappa.systems.kpiTracker.models.organisationFit.SurveyCategory;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.MessageComposer;
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
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionListener;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "surveyCategoryView")
@SessionScoped
@ViewPath(path = HyperLinks.SURVEY_CATEGORY_VIEW)
public class SurveyCategoryView extends PaginatedTableView<SurveyCategory, SurveyCategoryService, SurveyCategoryService> {
    private SurveyCategoryService surveyCategoryService;
    private SurveyCategory selectedCategory;
    private RecordStatus recordStatus;

    @PostConstruct
    public void init() {
        this.surveyCategoryService = ApplicationContextProvider.getBean(SurveyCategoryService.class);
        super.setTotalRecords(surveyCategoryService.countInstances(new Search().addFilterEqual("recordStatus", RecordStatus.ACTIVE)));
        this.reloadFilterReset();
    }

    @Override
    public void reloadFilterReset(){
        super.setTotalRecords(surveyCategoryService.countInstances(new Search().addFilterEqual("recordStatus", RecordStatus.ACTIVE)));
        try {
            super.reloadFilterReset();
        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", e.getLocalizedMessage());
        }
    }

    @Override
    public void reloadFromDB(int i, int i1, Map<String, Object> map) throws Exception {
        super.setDataModels(surveyCategoryService.getInstances(new Search().addFilterEqual("recordStatus", RecordStatus.ACTIVE), i, i1));
        super.setTotalRecords(surveyCategoryService.countInstances(new Search().addFilterEqual("recordStatus", RecordStatus.ACTIVE)));
        System.out.println("Reloading from DB with offset=" + i + ", limit=" + i1);


    }

    @Override
    public List<ExcelReport> getExcelReportModels() {
        return Collections.emptyList();
    }

    @Override
    public String getFileName() {
        return "OrganisationFit_survey_category_report";
    }

    @Override
    public List<SurveyCategory> load(int i, int i1, Map<String, SortMeta> map, Map<String, FilterMeta> map1) {
        return getDataModels();
    }

    public void deleteSelectedCategory() {
        try {
            if (selectedCategory != null) {
                surveyCategoryService.deleteInstance(this.selectedCategory);
                MessageComposer.compose(
                        "Success",
                        "Category '" + this.selectedCategory.getName() + "' has been deleted."
                );
                this.selectedCategory = null;
                this.reloadFilterReset();
            } else {
                MessageComposer.failed(
                        "Error",
                        "No category was selected for deletion."
                );
            }
        } catch (OperationFailedException e) {
            throw new RuntimeException("Deletion Failed", e);
        }
    }


    public void setSelectedCategory(SurveyCategory selectedCategory) {
        this.selectedCategory = selectedCategory;
    }
}
