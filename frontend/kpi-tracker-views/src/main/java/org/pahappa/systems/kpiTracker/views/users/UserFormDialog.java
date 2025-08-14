package org.pahappa.systems.kpiTracker.views.users;

import java.util.*;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.AssignedUserService;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.model.Gender;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.Role;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
@ManagedBean(name = "userFormDialog", eager = true)
@Getter
@Setter
@SessionScoped
public class UserFormDialog extends DialogForm<User> {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(UserFormDialog.class.getSimpleName());
    private UserService userService;

    private List<Gender> listOfGenders;
    private List<Role> databaseRoles;
    private Set<Role> userRoles;
    private boolean edit;

    private List<Department> listOfDepartments;
    private Department selectedDepartment;
    private DepartmentService departmentService;
    private AssignedUserService assignedUserService;

    @PostConstruct
    public void init() {
        this.userService = ApplicationContextProvider.getBean(UserService.class);
        this.assignedUserService = ApplicationContextProvider.getBean(AssignedUserService.class);
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        this.listOfDepartments = departmentService.getAllDepartments();
        this.listOfGenders = Arrays.asList(Gender.values());
        this.databaseRoles = userService.getRoles();
}

    public UserFormDialog() {
        super(HyperLinks.USER_FORM_DIALOG, 700, 500);
    }

    @Override
    public void persist() throws ValidationFailedException {
        super.model.setRoles(userRoles);
        User savedUser =  this.userService.saveUser(super.model);

        // assigning the saved user to the selected department
        if(selectedDepartment != null && savedUser != null) {
            assignedUserService.assignUserToDepartment(savedUser, selectedDepartment);
        }
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new User();
//        setEdit(false);
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();

        // refresh departments every time the form is opened
        this.listOfDepartments = departmentService.getAllDepartments();

        if(super.model != null)
//            setEdit(true);
        this.userRoles = new HashSet<>(userService.getRoles(super.model, 0, 0));
    }
}
