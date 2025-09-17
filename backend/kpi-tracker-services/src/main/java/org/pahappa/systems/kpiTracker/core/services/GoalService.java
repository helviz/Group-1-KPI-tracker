package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.constants.GoalLevel;
import org.pahappa.systems.kpiTracker.models.goalCreation.Goal;
import org.pahappa.systems.kpiTracker.models.staff.Staff;

import java.util.List;

public interface GoalService extends GenericService<Goal> {

    List<Goal> getGoalsByLevel(GoalLevel goalLevel); // Fetch goals by specific level

    List<Goal> getGoalsByOwner(Staff owner, GoalLevel goalLevel); // Fetch goals by owner with eager loading

    List<Goal> getPotentialParentGoals(GoalLevel childLevel); // Fetch valid parent goals

    List<Goal> getChildGoals(Goal parentGoal); // For weight sum validation
}
