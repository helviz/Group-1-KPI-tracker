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
        // Initialize dates as null to keep datepicker empty
        super.model.setStartDate(null);
        super.model.setEndDate(null);
        System.out.println("Init - End Date: " + (super.model.getEndDate() != null ? super.model.getEndDate().toString() : "null"));
    }

    @Override
    public void persist() throws Exception {
        // Validate the sum of MBO and Org Fit weights as doubles
        double mboWeight = super.model.getBusinessGoalContribution(); // Defaults to 0.0 if not set
        double orgFitWeight = super.model.getOrganisationalFitScore(); // Defaults to 0.0 if not set
        double totalWeight = mboWeight + orgFitWeight;

        // Use a small epsilon to handle floating-point precision
        final double EPSILON = 0.001;
        if (Math.abs(totalWeight - 100.0) > EPSILON && totalWeight > 100.0) {
            UiUtils.ComposeFailure("Validation Error",
                    String.format("The sum of MBO Weight and Org Fit Weight must not exceed 100.0%%. Current sum: %.2f%%. Please correct the values.", totalWeight));
            throw new ValidationFailedException("The sum of MBO Weight and Org Fit Weight must not exceed 100.0%.");
        }

        try {
            this.goalPeriodService.saveInstance(super.model);

        } catch (ValidationFailedException e) {
            UiUtils.ComposeFailure("Validation Error", e.getLocalizedMessage());
        } catch (Exception e) {
            UiUtils.ComposeFailure("Action Failed", e.getMessage());
        }
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new GoalPeriod();
        // Keep dates null to ensure empty datepicker
        super.model.setStartDate(null);
        super.model.setEndDate(null);
        System.out.println("Reset - End Date: " + (super.model.getEndDate() != null ? super.model.getEndDate().toString() : "null"));
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        if (super.model != null && super.model.getId() != null) {
            setEdit(true);
        } else {
            if (super.model == null) {
                super.model = new GoalPeriod();
            }
            setEdit(false);
            // Ensure dates are null for new forms
            super.model.setStartDate(null);
            super.model.setEndDate(null);
        }
    }

    // Debugging method to check selected date
    public void printDate() {
        String message = "End Date: " + (super.model.getEndDate() != null ? super.model.getEndDate().toString() : "null");
        System.out.println(message);

    }
}