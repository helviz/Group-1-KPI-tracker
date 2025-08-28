package org.pahappa.systems.kpiTracker.views.activity;

import lombok.Getter;
import org.pahappa.systems.kpiTracker.core.services.ActivityService;
import org.pahappa.systems.kpiTracker.models.activity.Activity;
import org.pahappa.systems.kpiTracker.constants.ActivityStatus;
import org.pahappa.systems.kpiTracker.models.goalMgt.Goal;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.pahappa.systems.kpiTracker.views.dialogs.MessageComposer;
import org.pahappa.systems.kpiTracker.views.utils.FacesUtils;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import lombok.Setter;
import org.primefaces.PrimeFaces;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

@ManagedBean(name = "activityFormDialog")
@SessionScoped
@Getter
@Setter
@ViewPath(path = HyperLinks.ACTIVITY_FORM_DIALOG)
public class ActivityDialog extends DialogForm<Activity> implements Serializable {

    private static final long serialVersionUID = 1L;
    private transient ActivityService activityService;

    public ActivityDialog() {
        super(HyperLinks.ACTIVITY_FORM_DIALOG, 700, 500);
        this.activityService = ApplicationContextProvider.getBean(ActivityService.class);
        resetModal();
    }

    public void prepareNew(Goal goal) {
        if (goal == null) {
            FacesUtils.addError("Error", "A goal must be selected before adding an activity.");
            PrimeFaces.current().ajax().addCallbackParam("validationFailed", true);
            return;
        }

        super.model = new Activity();
        super.model.setGoal(goal);
        super.model.setStatus(ActivityStatus.NOT_STARTED);
    }

    @Override
    public void persist() {
        try {
            this.activityService.saveInstance(super.model);

        } catch (ValidationFailedException | OperationFailedException e) {
            MessageComposer.error( "Error", e.getMessage());
        }
    }

    public ActivityStatus[] getActivityStatuses() {
        return ActivityStatus.values();
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new Activity();
    }

    @Override
    public void save() {
        persist();
    }
}
