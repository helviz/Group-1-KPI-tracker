package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.models.activity.Activity;
import org.pahappa.systems.kpiTracker.models.goalMgt.IndividualGoal;
import org.sers.webutils.model.exception.ValidationFailedException;

import java.util.List;

public interface ActivityService extends GenericService<Activity> {

    /**
     * Retrieves all activities associated with a specific goal.
     * @param goal The goal to retrieve activities for.
     * @return A list of activities.
     * @throws ValidationFailedException If the goal is null.
     */
    List<Activity> getActivitiesForGoal(IndividualGoal goal) throws ValidationFailedException;
}