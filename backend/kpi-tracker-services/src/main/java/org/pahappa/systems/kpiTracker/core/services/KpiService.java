package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.models.kpi.KPI;
import org.pahappa.systems.kpiTracker.models.goalMgt.IndividualGoal;

import java.util.List;
import org.sers.webutils.model.exception.ValidationFailedException;

public interface KpiService extends GenericService<KPI> {
    /**
     * Retrieves all KPIs associated with a specific individual goal.
     * 
     * @param goal The individual goal to retrieve KPIs for.
     * @return A list of KPIs.
     * @throws ValidationFailedException If the goal is null.
     */
    List<KPI> getKpisForGoal(IndividualGoal goal) throws ValidationFailedException;
}