package org.pahappa.systems.kpiTracker.views.users;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.CustomService;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.model.security.Role;
import org.sers.webutils.server.core.service.RoleService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
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


    @PostConstruct
    public void init() {
        this.roleService = ApplicationContextProvider.getBean(RoleService.class);
        this.customService = ApplicationContextProvider.getBean(CustomService.class);

    }

    public RoleFormDialog() {
        super(HyperLinks.ROLE_FORM_DIALOG, 700, 450);
    }

    @Override
    public void persist() {
        try {
            roleService.saveRole(super.model);
            UiUtils.showMessageBox("Action Success!", "Role updated");
        } catch (Exception e) {
            e.printStackTrace();
            UiUtils.showMessageBox("Action Failed!", e.getMessage());
        }
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new Role();
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
    }
}