package org.pahappa.systems.kpiTracker.views.users;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.CustomService;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.MessageComposer;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.Role;
import org.sers.webutils.server.core.service.RoleService;
import org.sers.webutils.server.core.service.PermissionService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.model.security.Permission;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.Logger;

/**
 *
 */
@ManagedBean(name = "roleFormDialog", eager = true)
@Getter
@Setter
@SessionScoped
public class RoleFormDialog extends DialogForm<Role> {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(RoleFormDialog.class.getSimpleName());
    private RoleService roleService;
    private CustomService customService;
    private PermissionService permissionService;


    @PostConstruct
    public void init() {
        this.roleService = ApplicationContextProvider.getBean(RoleService.class);
        this.customService = ApplicationContextProvider.getBean(CustomService.class);
        this.permissionService = ApplicationContextProvider.getBean(PermissionService.class);

    }

    public RoleFormDialog() {
        super(HyperLinks.ROLE_FORM_DIALOG, 700, 450);
    }

    @Override
    public void persist() {
        try {
            roleService.saveRole(super.model);
            MessageComposer.compose("Action Success!", "Role updated");
        } catch (Exception e) {
            e.printStackTrace();
            MessageComposer.compose("Action Failed!", e.getMessage());
        }
    }


    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new Role();
        // A new role created from the UI should at least have web access permission by default
        Permission webAccessPerm = this.permissionService.getPermissionByName("Web Access");
        if (webAccessPerm != null) {
            super.model.setPermissions(new HashSet<>(Arrays.asList(webAccessPerm)));
        }
    }

    @Override
    public void save(){
        try {
            if (model.isNew()) {
                Permission webAccessPerm = permissionService.getPermissionByName("perm_web_access");

                if (webAccessPerm == null) {
                    throw new OperationFailedException("System Error: Default permission 'perm_web_access' not found.");
                }

                model.setPermissions(new java.util.HashSet<>(java.util.Arrays.asList(webAccessPerm)));
            }

            roleService.saveRole(this.model);
            MessageComposer.compose("Success","Role saved successfully.");
            super.hide();
        } catch (ValidationFailedException | OperationFailedException e) {
            MessageComposer.compose("Error",e.getMessage());
        }
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
    }
}
