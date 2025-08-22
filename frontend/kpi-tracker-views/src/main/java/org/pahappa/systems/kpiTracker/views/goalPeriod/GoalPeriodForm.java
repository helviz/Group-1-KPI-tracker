package org.pahappa.systems.kpiTracker.views.goalPeriod;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.GoalPeriodService;
import org.pahappa.systems.kpiTracker.models.goalMgt.GoalPeriod;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@ManagedBean(name = "goalPeriodForm")
@SessionScoped
@ViewPath(path = HyperLinks.GOAL_PERIOD_FORM)
public class GoalPeriodForm extends DialogForm<GoalPeriod> implements Serializable {

    private static final long serialVersionUID = 1L;
    private boolean edit;

    private transient GoalPeriodService goalPeriodService;

    public GoalPeriodForm() {
        super(HyperLinks.GOAL_PERIOD_FORM, 700, 450);
    }

    @PostConstruct
    public void init() {
        this.goalPeriodService = ApplicationContextProvider.getBean(GoalPeriodService.class);

        super.model = new GoalPeriod();
        // Set default dates to the current time to avoid the JavaScript error
        super.model.setStartDate(new Date());
        super.model.setEndDate(new Date());
    }

    @Override
    public void persist() throws Exception {
        try {
            this.goalPeriodService.saveInstance(super.model);
        } catch (ValidationFailedException e) {
            UiUtils.ComposeFailure("Validation Error", e.getLocalizedMessage());
        } catch (Exception e) {
            //LOGGER.log(Level.SEVERE, "An unexpected error occurred", e);
            UiUtils.ComposeFailure("Action Failed", e.getMessage());
        }
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new GoalPeriod();
        // Set default dates to the current time to avoid the JavaScript error
        super.model.setStartDate(new Date());
        super.model.setEndDate(new Date());
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        if (super.model != null && super.model.getId() != null) {
            setEdit(true);
        } else {
            // If for some reason the model is null, ensure a new one is created.
            if (super.model == null) {
                super.model = new GoalPeriod();
            }
            setEdit(false);
        }
    }

}

