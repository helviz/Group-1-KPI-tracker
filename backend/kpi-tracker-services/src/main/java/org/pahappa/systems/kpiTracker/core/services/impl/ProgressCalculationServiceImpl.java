package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.services.ProgressCalculationService;
import org.pahappa.systems.kpiTracker.core.services.OrganisationGoalService;
import org.pahappa.systems.kpiTracker.core.services.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.core.services.TeamGoalService;
import org.pahappa.systems.kpiTracker.core.services.IndividualGoalService;
import org.pahappa.systems.kpiTracker.core.services.ProgressCalculationUtil;
import org.pahappa.systems.kpiTracker.models.goalMgt.OrganisationGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.TeamGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.IndividualGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.BaseGoal;
import org.pahappa.systems.kpiTracker.models.kpi.KPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;

@Service("progressCalculationService")
public class ProgressCalculationServiceImpl implements ProgressCalculationService {

    @Autowired
    private OrganisationGoalService organisationGoalService;

    @Autowired
    private DepartmentGoalService departmentGoalService;

    @Autowired
    private TeamGoalService teamGoalService;

    @Autowired
    private IndividualGoalService individualGoalService;

    @Override
    public double calculateOrganisationGoalProgress(OrganisationGoal organisationGoal) {
        if (organisationGoal == null || organisationGoal.getDepartmentGoals() == null
                || organisationGoal.getDepartmentGoals().isEmpty()) {
            return 0.0;
        }

        List<DepartmentGoal> departmentGoals = organisationGoal.getDepartmentGoals();
        return calculateWeightedAverage(departmentGoals);
    }

    @Override
    public double calculateDepartmentGoalProgress(DepartmentGoal departmentGoal) {
        if (departmentGoal == null || departmentGoal.getTeamGoals() == null
                || departmentGoal.getTeamGoals().isEmpty()) {
            return 0.0;
        }

        List<TeamGoal> teamGoals = departmentGoal.getTeamGoals();
        return calculateWeightedAverage(teamGoals);
    }

    @Override
    public double calculateTeamGoalProgress(TeamGoal teamGoal) {
        if (teamGoal == null || teamGoal.getIndividualGoals() == null || teamGoal.getIndividualGoals().isEmpty()) {
            return 0.0;
        }

        List<IndividualGoal> individualGoals = teamGoal.getIndividualGoals();
        return calculateWeightedAverage(individualGoals);
    }

    @Override
    public double calculateIndividualGoalProgress(IndividualGoal individualGoal) {
        if (individualGoal == null || individualGoal.getKpis() == null || individualGoal.getKpis().isEmpty()) {
            return 0.0;
        }

        List<KPI> kpis = individualGoal.getKpis();
        BigDecimal totalProgress = BigDecimal.ZERO;
        int kpiCount = kpis.size();

        for (KPI kpi : kpis) {
            if (kpi.getProgressPercentage() != null) {
                totalProgress = totalProgress.add(kpi.getProgressPercentage());
            }
        }

        if (kpiCount == 0) {
            return 0.0;
        }

        return totalProgress.divide(BigDecimal.valueOf(kpiCount), 2, RoundingMode.HALF_UP).doubleValue();
    }

    @Override
    public double calculateWeightedAverage(List<? extends BaseGoal> goals) {
        return ProgressCalculationUtil.calculateWeightedAverage(goals);
    }

    @Override
    public boolean rollupProgressFromIndividualGoal(IndividualGoal individualGoal) {
        try {
            if (individualGoal == null) {
                return false;
            }

            // Calculate individual goal progress from KPIs
            double individualProgress = calculateIndividualGoalProgress(individualGoal);
            individualGoal.updateProgress(new BigDecimal(individualProgress));
            individualGoalService.saveInstance(individualGoal);

            // Roll up to Team Goal
            TeamGoal teamGoal = individualGoal.getParentGoal();
            if (teamGoal != null) {
                double teamProgress = calculateTeamGoalProgress(teamGoal);
                teamGoal.updateProgress(new BigDecimal(teamProgress));
                teamGoalService.saveInstance(teamGoal);

                // Roll up to Department Goal
                DepartmentGoal departmentGoal = teamGoal.getParentGoal();
                if (departmentGoal != null) {
                    double departmentProgress = calculateDepartmentGoalProgress(departmentGoal);
                    departmentGoal.updateProgress(new BigDecimal(departmentProgress));
                    departmentGoalService.saveInstance(departmentGoal);

                    // Roll up to Organisation Goal
                    OrganisationGoal organisationGoal = departmentGoal.getParentGoal();
                    if (organisationGoal != null) {
                        double organisationProgress = calculateOrganisationGoalProgress(organisationGoal);
                        organisationGoal.updateProgress(new BigDecimal(organisationProgress));
                        organisationGoalService.saveInstance(organisationGoal);
                    }
                }
            }

            return true;
        } catch (Exception e) {
            // Log the error in a real application
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public double calculateOverallOrganisationalProgress() {
        try {
            List<OrganisationGoal> organisationGoals = organisationGoalService.findAllActive();
            if (organisationGoals == null || organisationGoals.isEmpty()) {
                return 0.0;
            }

            BigDecimal totalProgress = BigDecimal.ZERO;
            int goalCount = organisationGoals.size();

            for (OrganisationGoal goal : organisationGoals) {
                if (goal.getProgress() != null) {
                    totalProgress = totalProgress.add(goal.getProgress());
                }
            }

            if (goalCount == 0) {
                return 0.0;
            }

            return totalProgress.divide(BigDecimal.valueOf(goalCount), 2, RoundingMode.HALF_UP).doubleValue();
        } catch (Exception e) {
            // Log the error in a real application
            e.printStackTrace();
            return 0.0;
        }
    }
}