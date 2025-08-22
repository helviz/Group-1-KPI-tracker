package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.models.goalMgt.Goal;
import org.pahappa.systems.kpiTracker.models.kpi.KPI;

import java.util.List;
import org.sers.webutils.model.exception.ValidationFailedException;

public interface KpiService extends GenericService<KPI> {
    /**
     * Retrieves all KPIs associated with a specific goal.
     * @param goal The goal to retrieve KPIs for.
     * @return A list of KPIs.
     * @throws ValidationFailedException If the goal is null.
     */
    List<KPI> getKpisForGoal(Goal goal) throws ValidationFailedException;
}