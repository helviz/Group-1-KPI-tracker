package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.models.goalMgt.Goal;

import java.util.Set;

public interface ProgressCalculationService {
    public double calculateWeightedAverage(Set<Goal> childGoals);

}
