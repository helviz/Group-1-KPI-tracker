package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.services.ProgressCalculationService; // Make sure to implement the interface
import org.pahappa.systems.kpiTracker.models.goalMgt.Goal;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service("progressCalculationService") // It is best practice to give the bean a name
public class ProgressCalculationServiceImpl implements ProgressCalculationService {
    // It should implement your interface

    @Override // Add the @Override annotation
    public double calculateWeightedAverage(Set<Goal> childGoals) {
        if (childGoals == null || childGoals.isEmpty()) {
            return 0.0;
        }

        double totalWeightedProgress = 0.0;
        double totalWeight = 0.0;

        // Using a traditional for-each loop which is compatible with Java 1.7
        for (Goal goal : childGoals) {
            if (goal != null && goal.getProgress() != null && goal.getGoalEvaluationWeight() != null) {
                totalWeightedProgress += goal.getProgress() * goal.getGoalEvaluationWeight();
                totalWeight += goal.getGoalEvaluationWeight();
            }
        }

        if (totalWeight == 0) {
            return 0.0;
        }

        return totalWeightedProgress / totalWeight;
    }
}