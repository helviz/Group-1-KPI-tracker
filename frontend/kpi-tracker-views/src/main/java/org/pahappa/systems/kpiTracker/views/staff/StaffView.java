package org.pahappa.systems.kpiTracker.views.staff;

import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.Filter;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.pahappa.systems.kpiTracker.core.services.StaffService;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.dialogs.MessageComposer;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.Gender;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.Role;
import org.sers.webutils.model.security.User;
import org.sers.webutils.model.utils.SearchField;
import org.sers.webutils.server.core.service.RoleService;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ManagedBean(name = "staffView")
@Getter
@Setter
@SessionScoped
@ViewPath(path = HyperLinks.STAFF_VIEW)
public class StaffView extends PaginatedTableView<Staff, StaffView, StaffView> implements Serializable {

    private static final long serialVersionUID = 1L;
    private StaffService staffService;
    private RoleService roleService;

    private String searchTerm;
    private Search search;

    private List<Gender> genders = new ArrayList<>();
    private Gender selectedGender;
    private Date createdFrom, createdTo;

    private Staff selectedStaff;
    private List<Role> rolesList = new ArrayList<>();
    private Set<Role> selectedRolesList = new HashSet<>();
    private List<SearchField> searchFields, selectedSearchFields;

    private boolean showSystemUsersOnly = false;

    @PostConstruct
    public void init() {
        this.staffService = ApplicationContextProvider.getBean(StaffService.class);
        roleService = ApplicationContextProvider.getBean(RoleService.class);

        this.rolesList = roleService.getRoles();
        this.genders = Arrays.asList(Gender.values());
        this.reloadFilterReset();
    }

    public void doSearch() {
        this.reloadFilterReset();
    }

    @Override
    public void reloadFilterReset() {
        this.searchFields = Arrays.asList(
                new SearchField("First Name", "user.firstName"),
                new SearchField("Last Name", "user.lastName"),
                new SearchField("Phone Number", "user.phoneNumber"),
                new SearchField("Username", "user.username"),
                new SearchField("Email Address", "user.emailAddress")
        );
        this.search = composeStaffSearch();
        super.setTotalRecords(staffService.countInstances(this.search));
        try {
            super.reloadFilterReset();
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).info(e.getMessage());
        }
    }



    public Search composeStaffSearch() {
        Search search = new Search(Staff.class);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);

        if (StringUtils.isNotBlank(searchTerm)) {
            List<Filter> filters = new ArrayList<>();
            filters.add(Filter.ilike("firstName", "%" + searchTerm + "%"));
            filters.add(Filter.ilike("lastName", "%" + searchTerm + "%"));
            filters.add(Filter.ilike("email", "%" + searchTerm + "%"));
            search.addFilter(Filter.or(filters.toArray(new Filter[0])));
        }

        if (selectedGender != null) {
            search.addFilterEqual("gender", selectedGender);
        }

        if (createdFrom != null) {
            search.addFilterGreaterOrEqual("dateCreated", createdFrom);
        }

        if (createdTo != null) {
            search.addFilterLessOrEqual("dateCreated", createdTo);
        }

        if (showSystemUsersOnly) {
            search.addFilterNotNull("user");
        }

        search.addSort("firstName", false);
        return search;
    }

    public void activateUserAccount(Staff staff) {
        try {
            Staff freshStaff = staffService.getInstanceByID(staff.getId());
            if (freshStaff != null && freshStaff.getUser() == null) {
                if (freshStaff.getDepartment() == null) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Staff must have a department before a user account can be created."));
                    return;
                }
                staffService.createUser(freshStaff);
                UiUtils.showMessageBox("Success", "User account for " + freshStaff.getFullName() + " has been activated successfully. An email with a temporary password has been sent.");
                reloadFilterReset();
            } else if (freshStaff != null && freshStaff.getUser() != null) {
                UiUtils.showMessageBox("Info", "This staff member already has an active user account.");
            } else {
                UiUtils.showMessageBox("Error", "Staff member not found or already deleted.");
            }
        } catch (ValidationFailedException | OperationFailedException e) {
            MessageComposer.error("Account Activation Failed", e.getMessage());
        } catch (Exception e) {
            MessageComposer.error("Error", "An unexpected error occurred during account activation.");
            Logger.getLogger(this.getClass().getName()).log(java.util.logging.Level.SEVERE, "Error activating user account", e);
        }
    }

    public void deleteSelectedStaff(Staff staff) {
        try {
            staffService.deleteInstance(staff);
            UiUtils.showMessageBox("Action successful", "User has been deactivated.");
            reloadFilterReset();
        } catch (OperationFailedException ex) {
            UiUtils.ComposeFailure("Action failed", ex.getLocalizedMessage());
        }
    }

    @Override
    public void reloadFromDB(int offset, int limit, Map<String, Object> filters) {
        this.search = composeStaffSearch();
        super.setDataModels(staffService.getInstances(this.search, offset, limit));
    }

    @Override
    public List<Staff> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        return getDataModels();
    }


    @Override
    public List<ExcelReport> getExcelReportModels() {
        return Collections.emptyList();
    }

    @Override
    public String getFileName() {
        return null;
    }

    public String getStaffDepartmentName(Staff staff) {
        if (staff == null || staff.getDepartment() == null) {
            return "No Department Assigned";
        }
        return staff.getDepartment().getName();
    }

    public String getRolesAsString(Staff staff) {
        if (staff == null || staff.getUser() == null || staff.getUser().getRoles() == null || staff.getUser().getRoles().isEmpty()) {
            return "No Roles Assigned";
        }
        return staff.getUser().getRoles().stream()
                .map(Role::getName)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "));
    }
}