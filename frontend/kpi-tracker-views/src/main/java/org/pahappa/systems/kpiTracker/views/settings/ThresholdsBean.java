package org.pahappa.systems.kpiTracker.views.settings;

import org.pahappa.systems.kpiTracker.core.services.PerformanceThresholdsService;
import org.pahappa.systems.kpiTracker.models.settings.PerformanceThresholds;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.Dashboard;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.client.views.presenters.WebFormView;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "thresholdsBean")
@SessionScoped
@ViewPath(path = HyperLinks.SETTING)
public class ThresholdsBean extends WebFormView<PerformanceThresholds, ThresholdsBean, Dashboard> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private PerformanceThresholdsService performanceThresholdsService;

    @PostConstruct
    public void init() {
        this.performanceThresholdsService = ApplicationContextProvider.getBean(PerformanceThresholdsService.class);
        initializeModel();
    }

    @Override
    public void beanInit() {
        this.performanceThresholdsService = ApplicationContextProvider.getBean(PerformanceThresholdsService.class);
    }

    @Override
    public void pageLoadInit() {
        initializeModel();
    }

    private void initializeModel() {
        if (super.model == null) {
            try {
                PerformanceThresholds existingThresholds = this.performanceThresholdsService
                        .getActivePerformanceThresholds();
                if (existingThresholds != null) {
                    super.model = existingThresholds;
                } else {
                    super.model = new PerformanceThresholds();
                    // Set default values
                    super.model.setRewardEligibilityThreshold(95.0);
                    super.model.setPipInitiationThreshold(70.0);
                }
            } catch (Exception e) {
                // If service fails, create with defaults
                super.model = new PerformanceThresholds();
                super.model.setRewardEligibilityThreshold(95.0);
                super.model.setPipInitiationThreshold(70.0);
            }
        }
    }

    @Override
    public void persist() throws Exception {
        try {
            // Validate before saving
            if (!validateThresholds()) {
                return; // Validation failed, don't proceed
            }

            this.performanceThresholdsService.saveInstance(super.model);
            UiUtils.showMessageBox("Performance thresholds updated", "Action Successful");
        } catch (org.sers.webutils.model.exception.ValidationFailedException e) {
            // Handle validation errors gracefully
            UiUtils.showMessageBox(e.getMessage(), "Validation Error");
        } catch (org.sers.webutils.model.exception.OperationFailedException e) {
            // Handle operation errors gracefully
            UiUtils.showMessageBox(e.getMessage(), "Operation Failed");
        } catch (Exception e) {
            // Handle any other unexpected errors
            UiUtils.showMessageBox("An unexpected error occurred: " + e.getMessage(), "Error");
        }
    }

    /**
     * Validate the threshold values before saving
     * 
     * @return true if validation passes, false otherwise
     */
    private boolean validateThresholds() {
        if (super.model == null) {
            UiUtils.showMessageBox("Model is not initialized", "Error");
            return false;
        }

        double rewardThreshold = super.model.getRewardEligibilityThreshold();
        double pipThreshold = super.model.getPipInitiationThreshold();

        // Check for valid values (double primitives can't be null, but we can check for
        // NaN)
        if (Double.isNaN(rewardThreshold) || Double.isNaN(pipThreshold)) {
            UiUtils.showMessageBox("Both threshold values are required", "Validation Error");
            return false;
        }

        // Check range (0-100)
        if (rewardThreshold < 0 || rewardThreshold > 100) {
            UiUtils.showMessageBox("Reward eligibility threshold must be between 0 and 100", "Validation Error");
            return false;
        }

        if (pipThreshold < 0 || pipThreshold > 100) {
            UiUtils.showMessageBox("PIP initiation threshold must be between 0 and 100", "Validation Error");
            return false;
        }

        // Check that reward threshold is greater than PIP threshold
        if (rewardThreshold <= pipThreshold) {
            UiUtils.showMessageBox("Reward eligibility threshold must be greater than PIP initiation threshold",
                    "Validation Error");
            return false;
        }

        return true;
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new PerformanceThresholds();
        // Set default values
        super.model.setRewardEligibilityThreshold(95.0);
        super.model.setPipInitiationThreshold(70.0);
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
    }

    @Override
    public String getViewUrl() {
        return this.getViewPath();
    }
}