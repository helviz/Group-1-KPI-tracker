package org.pahappa.systems.kpiTracker.views.department;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import java.util.List;


@ManagedBean(name = "departmentFormDialog", eager = true)
@Getter
@Setter
@ViewPath(path= HyperLinks.DEPARTMENT_FORM_DIALOG)
@SessionScoped
public class DepartmentFormDialog  extends DialogForm<Department> {

    private static final long serialVersionUID = 1L;
    private DepartmentService departmentService;
    private UserService userService;

    private List<User> availableUsers; // Add a list to hold users for the dropdown

    private boolean edit;

    @PostConstruct
    public void init() {
        try {

            this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
            this.userService = ApplicationContextProvider.getBean(UserService.class);
            this.availableUsers = this.userService.getUsers();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * The constructor now initializes the model object immediately,
     * ensuring it is never null when the JSF page is rendered.
     */
    public DepartmentFormDialog() {
        super(HyperLinks.DEPARTMENT_FORM_DIALOG, 700, 450);
    }

    @Override
    public void persist() throws ValidationFailedException, OperationFailedException {
        this.departmentService.saveInstance(super.model);
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new Department();
        setEdit(false);
    }

    /**
     * This method is called when you want to load an existing department for editing.
     * The model is set from outside (e.g., using f:setPropertyActionListener).
     * This logic will handle both creating a new model if one doesn't exist and
     * setting the edit flag if it does.
     */
    @Override
    public void setFormProperties() {
        super.setFormProperties();
        if (super.model != null && super.model.getId() != null) {
            setEdit(true);
        } else {
            // If for some reason the model is null, ensure a new one is created.
            if (super.model == null) {
                super.model = new Department();
            }
            setEdit(false);
        }
    }
}