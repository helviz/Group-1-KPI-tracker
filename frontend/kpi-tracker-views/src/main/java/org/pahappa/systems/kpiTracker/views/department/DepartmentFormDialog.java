package org.pahappa.systems.kpiTracker.views.department;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;


@ManagedBean(name = "departmentFormDialog", eager = true)
@Getter
@Setter
@ViewPath(path= HyperLinks.DEPARTMENT_FORM_DIALOG)
@SessionScoped
public class DepartmentFormDialog  extends DialogForm<Department> {

    private static final long serialVersionUID = 1L;
//    private static final Logger LOGGER = Logger.getLogger(UserFormDialog.class.getSimpleName());
    private DepartmentService departmentService;

    private boolean edit;

    @PostConstruct
    public void init() {
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);

    }

    public DepartmentFormDialog() {
        super(HyperLinks.DEPARTMENT_FORM_DIALOG, 700, 450);
    }

    @Override
    public void persist() throws ValidationFailedException {
//        this.departmentService.saveInstance(Department);
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new Department();
        setEdit(false);
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        if(super.model != null)
            setEdit(true);
//        this.userRoles = new HashSet<>(userService.getRoles(super.model, 0, 0));
    }
}
