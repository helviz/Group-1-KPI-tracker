package org.pahappa.systems.kpiTracker.views.staff;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.StaffService;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.client.views.presenters.WebFormView;
import org.sers.webutils.model.Gender;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.security.Role;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.core.utils.TelephoneNumberUtils;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@ManagedBean(name = "staffForm")
@Getter
@Setter
@SessionScoped
@ViewPath(path = HyperLinks.STAFF_FORM)
public class StaffForm extends WebFormView<Staff, StaffForm, StaffView> {

    private static final long serialVersionUID = 1L;
    private List<Gender> listOfGenders;
    private UserService userService;
    private StaffService staffService;
    private DepartmentService departmentService;

    private List<Role> databaseRoles;
    private List<Role> userRoles = new ArrayList<>();
    private List<Department> listOfDepartments;
    private Department selectedDepartment;
    private User userModel = new User();

    @Override
    @PostConstruct
    public void beanInit() {
        this.userService = ApplicationContextProvider.getBean(UserService.class);
        this.staffService = ApplicationContextProvider.getBean(StaffService.class);
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        this.listOfGenders = new ArrayList<>(Arrays.asList(Gender.values()));
    }

    @Override
    public void pageLoadInit() {
        this.databaseRoles = userService.getRoles();
        this.listOfDepartments = departmentService.getAllDepartments();
    }

    @Override
    public void persist() throws Exception {
        // 1. Prepare and save the User object
        this.userModel.setRoles(new HashSet<>(this.userRoles));
        this.userModel.addRole(userService.getRoleByRoleName(Role.DEFAULT_WEB_ACCESS_ROLE));
        this.userModel.setPhoneNumber(TelephoneNumberUtils.getValidTelephoneNumber(this.userModel.getPhoneNumber()));
        User savedUser = userService.saveUser(this.userModel);

        // 2. Prepare and save the Staff object
        super.getModel().setUser(savedUser);
        super.getModel().setDepartment(this.selectedDepartment);
        staffService.saveStaff(super.getModel());
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.setModel(new Staff());
        this.userModel = new User();
        this.userRoles = new ArrayList<>();
        this.selectedDepartment = null;
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        if (super.getModel() != null && super.getModel().getId() != null) {
            this.userModel = super.getModel().getUser();
            this.userRoles = new ArrayList<>(this.userModel.getRoles());
            this.selectedDepartment = super.getModel().getDepartment();
        }
    }

    public void deleteStaff(Staff staff) throws OperationFailedException {
        staffService.deleteInstance(staff);
    }
}