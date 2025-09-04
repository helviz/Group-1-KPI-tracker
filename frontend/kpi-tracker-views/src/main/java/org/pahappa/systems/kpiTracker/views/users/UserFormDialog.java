package org.pahappa.systems.kpiTracker.views.users;

import java.util.*;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped; // Changed from SessionScoped

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.AssignedUserService;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.pahappa.systems.kpiTracker.views.utils.FacesUtils;
import org.sers.webutils.model.Gender;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.Role;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

@ManagedBean(name = "userFormDialog", eager = true)
@SessionScoped // Changed to ViewScoped for better dialog state management
@Setter
@Getter
public class UserFormDialog extends DialogForm<User> {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(UserFormDialog.class.getSimpleName());
    private UserService userService;
    private DepartmentService departmentService;
    private AssignedUserService assignedUserService;

    private List<Gender> listOfGenders;
    private List<Role> databaseRoles;
    private Set<Role> userRoles;
    private boolean edit;
    private List<Department> listOfDepartments;
    private Department selectedDepartment;

    @PostConstruct
    public void init() {
        this.userService = ApplicationContextProvider.getBean(UserService.class);
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        this.assignedUserService = ApplicationContextProvider.getBean(AssignedUserService.class);
        this.listOfDepartments = departmentService.getAllDepartments();
        this.listOfGenders = Arrays.asList(Gender.values());
        this.databaseRoles = userService.getRoles();
        this.userRoles = new HashSet<>(); // Initialize to avoid null
    }

    public UserFormDialog() {
        super(HyperLinks.USER_FORM_DIALOG, 650, 700);
    }

    public void onRolesChange() {
        // Handle role selection change
        if (userRoles != null && !userRoles.isEmpty()) {
            LOGGER.info("Selected roles: " + userRoles);
            // Add any additional logic (e.g., update dependent fields)
        }
    }

    @Override
    public void persist() throws ValidationFailedException {
        super.model.setRoles(userRoles);
        User savedUser = this.userService.saveUser(super.model);
        if (selectedDepartment != null && savedUser != null) {
            assignedUserService.assignUserToDepartment(savedUser, selectedDepartment);
        }

        // Add custom success message based on whether it's edit or create
        if (edit) {
            FacesUtils.addInfo("Success", "User updated successfully!");
        } else {
            FacesUtils.addInfo("Success", "User created successfully!");
        }
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new User();
        this.userRoles = new HashSet<>(); // Reset roles
        this.selectedDepartment = null; // Reset department
        this.edit = false; // Explicitly reset edit mode
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        this.listOfDepartments = departmentService.getAllDepartments(); // Refresh departments
        if (super.model != null) {
            this.edit = true; // Set edit mode for existing user
            this.userRoles = new HashSet<>(userService.getRoles(super.model, 0, 0));
            // Optionally set selectedDepartment if user is assigned to one
        } else {
            this.edit = false; // Create mode
            this.userRoles = new HashSet<>();
        }
    }

}