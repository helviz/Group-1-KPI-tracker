package org.pahappa.systems.kpiTracker.views.staff;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.StaffService;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.primefaces.PrimeFaces;
import org.pahappa.systems.kpiTracker.views.utils.FacesUtils;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.Role;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

@ManagedBean(name = "staffFormDialog", eager = true)
@ViewScoped
@Setter
@Getter
public class StaffFormDialog extends DialogForm<Staff> {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(StaffFormDialog.class.getSimpleName());
    private DepartmentService departmentService;
    private StaffService staffService;


    private boolean edit;
    private List<Department> listOfDepartments;
    private Department selectedDepartment;

    @PostConstruct
    public void init() {
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        this.staffService = ApplicationContextProvider.getBean(StaffService.class);
        this.listOfDepartments = departmentService.getAllDepartments();
    }

    public StaffFormDialog() {
        super(HyperLinks.STAFF_FORM_DIALOG, 650, 700);
    }


    @Override
    public void persist() {
        try {
            super.model.setDepartment(this.selectedDepartment);
            Staff savedStaff = this.staffService.saveInstance(super.model);

            if (edit) {
                FacesUtils.addInfo("Success", "Staff details updated successfully!");
            } else {
                FacesUtils.addInfo("Success", "Staff created successfully!");
            }
            PrimeFaces.current().dialog().closeDynamic(savedStaff);
        } catch (ValidationFailedException e) {
            FacesUtils.addError("Validation Error", e.getMessage());
        } catch (OperationFailedException e) {
            FacesUtils.addError("Operation Failed", e.getMessage());
        } catch (Exception e) {
            FacesUtils.addError("An unexpected error occurred", "Please contact system support.");
            LOGGER.log(Level.SEVERE, "Unexpected error during staff persistence", e);
        }
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new Staff();
        this.selectedDepartment = null; // Reset department
        this.edit = false; // Explicitly reset edit mode
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        this.listOfDepartments = departmentService.getAllDepartments();
        if (super.model != null && super.model.getId() != null) {
            this.edit = true; // Set edit mode for existing user
            this.selectedDepartment = super.model.getDepartment();
        } else {
            this.edit = false; // Create mode
            resetModal();
        }
    }

}