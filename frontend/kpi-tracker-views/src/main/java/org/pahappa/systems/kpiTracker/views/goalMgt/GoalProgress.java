package org.pahappa.systems.kpiTracker.views.goalMgt;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.GoalService;
import org.pahappa.systems.kpiTracker.core.services.KpiService;
import org.pahappa.systems.kpiTracker.models.goalMgt.Goal;
import org.pahappa.systems.kpiTracker.models.kpi.KPI;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Getter
@Setter
@ManagedBean(name = "goalProgressBean")
@ViewScoped
public class GoalProgress {

    private static final long serialVersionUID = 1L;
    private GoalService goalService;
    private KpiService kpiService;

    private String goalId;
    private Goal currentGoal;
    private List<KPI> kpis;

    @PostConstruct
    public void init() {
        this.goalService = ApplicationContextProvider.getBean(GoalService.class);
        this.kpiService = ApplicationContextProvider.getBean(KpiService.class);
    }

    public void loadGoalData() {
        if (goalId != null && !goalId.isEmpty()) {
            this.currentGoal = goalService.getInstanceByID(goalId);
            if (this.currentGoal != null) {
                // Verify this is an Individual goal
                if (!this.currentGoal.isIndividualGoal()) {
                    UiUtils.ComposeFailure("Error", "Progress can only be updated for Individual goals");
                    return;
                }

                // Fetch all the KPIs related to this specific goal
                try {
                    this.kpis = kpiService.getKpisForGoal(this.currentGoal);
                } catch (ValidationFailedException e) {
                    UiUtils.ComposeFailure("Error", "Failed to load KPIs: " + e.getMessage());
                }
            } else {
                UiUtils.ComposeFailure("Error", "Goal not found");
            }
        } else {
            UiUtils.ComposeFailure("Error", "No goal ID provided");
        }
    }

    public void saveProgress() {
        try {
            if (this.currentGoal == null) {
                UiUtils.ComposeFailure("Error", "No goal selected");
                return;
            }

            if (!this.currentGoal.isIndividualGoal()) {
                UiUtils.ComposeFailure("Error", "Progress can only be updated for Individual goals");
                return;
            }

            // Step 1: Save all updated KPI values first
            for (KPI kpi : kpis) {
                kpiService.saveInstance(kpi);
            }

            // Step 2: Calculate the new progress for the Individual Goal
            double newProgress = calculateIndividualGoalProgress();
            this.currentGoal.setProgress(newProgress);

            // Step 3: SAVE the Goal. This triggers the entire chain reaction!
            goalService.saveInstance(this.currentGoal);

//            UiUtils.ComposeSuccess("Success",
//                    "Progress updated successfully! The changes have been propagated up the goal hierarchy.");

        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", "Failed to update progress: " + e.getMessage());
        }
    }

    /**
     * Calculate progress for Individual Goal based on its KPIs
     */
    private double calculateIndividualGoalProgress() {
        if (kpis == null || kpis.isEmpty()) {
            return 0.0;
        }

        BigDecimal totalProgress = BigDecimal.ZERO;
        int kpiCount = kpis.size();

        for (KPI kpi : kpis) {
            totalProgress = totalProgress.add(kpi.getProgressPercentage());
        }

        // Calculate average progress
        return totalProgress
                .divide(BigDecimal.valueOf(kpiCount), 2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * Add new KPI to the current goal
     */
    public void addKpi() {
        try {
            if (this.currentGoal == null) {
                UiUtils.ComposeFailure("Error", "No goal selected");
                return;
            }

            // This would typically open a dialog to add a new KPI
            // For now, we'll just show a message
//            UiUtils.ComposeSuccess("Info", "Add KPI functionality will be implemented");

        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", "Failed to add KPI: " + e.getMessage());
        }
    }

    /**
     * Get the progress status based on percentage
     */
    public String getProgressStatus(double progress) {
        if (progress >= 100) {
            return "COMPLETED";
        } else if (progress >= 75) {
            return "ON_TRACK";
        } else if (progress >= 50) {
            return "AT_RISK";
        } else {
            return "BEHIND";
        }
    }

    /**
     * Get the severity class for progress status
     */
    public String getProgressSeverity(double progress) {
        if (progress >= 100) {
            return "success";
        } else if (progress >= 75) {
            return "success";
        } else if (progress >= 50) {
            return "warn";
        } else {
            return "danger";
        }
    }
}