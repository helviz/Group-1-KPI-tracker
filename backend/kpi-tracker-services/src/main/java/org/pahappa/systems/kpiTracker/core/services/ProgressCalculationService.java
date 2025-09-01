package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.models.goalMgt.OrganisationGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.TeamGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.IndividualGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.BaseGoal;

import java.util.List;
import java.util.Set;

public interface ProgressCalculationService {

    /**
     * Calculate weighted average progress for Organisation Goals based on
     * Department Goals
     */
    double calculateOrganisationGoalProgress(OrganisationGoal organisationGoal);

    /**
     * Calculate weighted average progress for Department Goals based on Team Goals
     */
    double calculateDepartmentGoalProgress(DepartmentGoal departmentGoal);

    /**
     * Calculate weighted average progress for Team Goals based on Individual Goals
     */
    double calculateTeamGoalProgress(TeamGoal teamGoal);

    /**
     * Calculate Individual Goal progress based on KPIs
     */
    double calculateIndividualGoalProgress(IndividualGoal individualGoal);

    /**
     * Calculate weighted average progress for any collection of goals
     * 
     * @param goals List of goals to calculate progress for
     * @return Weighted average progress
     */
    double calculateWeightedAverage(List<? extends BaseGoal> goals);

    /**
     * Calculate progress roll-up from Individual Goal up to Organisation Goal
     * 
     * @param individualGoal The individual goal to start roll-up from
     * @return True if roll-up was successful
     */
    boolean rollupProgressFromIndividualGoal(IndividualGoal individualGoal);

    /**
     * Calculate overall organisational progress across all goal levels
     * 
     * @return Overall organisational progress percentage
     */
    double calculateOverallOrganisationalProgress();
}
