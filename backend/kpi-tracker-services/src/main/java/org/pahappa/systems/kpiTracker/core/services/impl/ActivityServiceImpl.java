package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.services.ActivityService;
import org.pahappa.systems.kpiTracker.models.activity.Activity;
import org.pahappa.systems.kpiTracker.constants.ActivityStatus;
import org.pahappa.systems.kpiTracker.models.goalMgt.Goal;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("activityService")
@Transactional
public class ActivityServiceImpl extends GenericServiceImpl<Activity> implements ActivityService {

    @Override
    public Activity saveInstance(Activity activity) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(activity, "Activity details cannot be null");
        Validate.notNull(activity.getGoal(), "Activity must be attached to a goal");
        Validate.hasText(activity.getName(), "Activity name is required");

        if (isDuplicate(activity, "name", activity.getName())) {
            throw new ValidationFailedException("An activity with the same name already exists for this goal.");
        }

        if (activity.isNew()) {
            activity.setStatus(ActivityStatus.NOT_STARTED);
        }
        return super.save(activity);
    }

    @Override
    public boolean isDeletable(Activity instance) throws OperationFailedException {
        // For now, we allow deletion.
        // Future logic could prevent deletion if the activity is part of a
        // completed/approved goal.
        return true;
    }

    @Override
    public List<Activity> getActivitiesForGoal(Goal goal) throws ValidationFailedException {
        Validate.notNull(goal, "Goal cannot be null");
        Search search = new Search();
        search.addFilterEqual("goal", goal);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        return super.search(search);
    }

    private boolean isDuplicate(Activity entity, String property, Object value) {
        Search search = new Search()
                .addFilterEqual(property, value)
                .addFilterEqual("goal", entity.getGoal())
                .addFilterEqual("recordStatus", RecordStatus.ACTIVE);

        if (!entity.isNew()) {
            search.addFilterNotEqual("id", entity.getId());
        }
        return super.count(search) > 0;
    }
}