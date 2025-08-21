package org.pahappa.systems.kpiTracker.views.goalLevel;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.GoalLevelService;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.models.goalMgt.GoalLevel;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.List;

@Setter
@Getter
@ManagedBean(name = "goalLevelForm")
@ViewPath(path= HyperLinks.GOAL_LEVEL_FORM_DIALOG)
@SessionScoped
public class GoalLevelForm extends DialogForm<GoalLevel> {

    private static final long serialVersionUID = 1L;
    private GoalLevelService goalLevelService;

    private boolean edit;

    @PostConstruct
    public void init() {
        try {
            this. goalLevelService = ApplicationContextProvider.getBean( GoalLevelService.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GoalLevelForm() {
        super(HyperLinks.GOAL_LEVEL_FORM_DIALOG, 400,400);
    }

    @Override
    public void persist() throws Exception {
        this.goalLevelService.saveInstance(super.model);

    }
    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new GoalLevel();
        setEdit(false);
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        if (super.model != null && super.model.getId() != null) {
            setEdit(true);
        } else {
            // If for some reason the model is null, ensure a new one is created.
            if (super.model == null) {
                super.model = new GoalLevel();
            }
            setEdit(false);
        }
    }
}
