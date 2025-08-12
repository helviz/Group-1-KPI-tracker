package org.pahappa.systems.kpiTracker.views.department;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.users.UsersView;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.utils.SearchField;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@ManagedBean(name ="departmentView")
@Getter
@Setter
@ViewScoped
@ViewPath(path = HyperLinks. DEPARTMENT_VIEW)
public class DepartmentView extends PaginatedTableView<Department, DepartmentService,DepartmentService> {
    public DepartmentService departmentService;
    private Department seletedDepartment;
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

    public void deleteSelectedDepartment(Department department) {
        try {
            departmentService.deleteInstance(seletedDepartment);
            UiUtils.showMessageBox("Action successful", "User has been deactivated.");
        } catch (OperationFailedException ex) {
            UiUtils.ComposeFailure("Action failed", ex.getLocalizedMessage());
            Logger.getLogger(UsersView.class.getName()).log(Level.SEVERE, null, ex);
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