package org.pahappa.systems.kpiTracker.views.department;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.models.goalMgt.GoalLevel;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.dialogs.MessageComposer;
import org.pahappa.systems.kpiTracker.views.users.UsersView;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.security.User;
import org.sers.webutils.model.utils.SearchField;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;
import java.util.Map;


@ManagedBean(name ="departmentView")
@Getter
@Setter
@ViewScoped
@ViewPath(path = HyperLinks. DEPARTMENT_VIEW)
public class DepartmentView extends PaginatedTableView<Department, DepartmentService,DepartmentService> {
    public DepartmentService departmentService;
    private Department selectedDepartment;
    private List<SearchField> searchFields, selectedSearchFields;

    @PostConstruct
    public void init() {
        departmentService = ApplicationContextProvider.getBean(DepartmentService.class);

           this.reloadFilterReset();
    }


    @Override
    public void reloadFromDB(int i, int i1, Map<String, Object> map) throws Exception {
        super.setDataModels(departmentService.getInstances(new Search().addFilterEqual("recordStatus", RecordStatus.ACTIVE), i, i1));
    }

    @Override
    public List<Department> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        return getDataModels();
    }

    @Override
    public void reloadFilterReset() {
        super.setTotalRecords(departmentService.countInstances(new Search()));
        try {
            super.reloadFilterReset();
        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", e.getLocalizedMessage());
        }

    }
    /**
     * Deletes the specific GoalPeriod passed from the UI.
     * @param selectedDepartment The record selected by the user in the data table.
     */
    public void deleteSelectedDepartment(Department selectedDepartment) {
        try {
            // We check the parameter directly. It's much safer.
            if (selectedDepartment != null) {
                departmentService.deleteInstance(selectedDepartment);
                MessageComposer.info("Success",
                        "Period '" + selectedDepartment.getName() + "' has been deleted.");
                this.reloadFilterReset();
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
    public List<ExcelReport> getExcelReportModels() {
        return null;
    }

    @Override
    public String getFileName() {
        return null;
    }
}