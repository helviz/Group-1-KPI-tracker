package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.services.GoalService;
import org.pahappa.systems.kpiTracker.models.goalMgt.Goal;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GoalServiceImpl extends GenericServiceImpl<Goal>implements GoalService {
    @Override
    public boolean isDeletable(Goal instance) throws OperationFailedException {
        return true;
    }

    @Override
    public Goal saveInstance(Goal entityInstance) throws ValidationFailedException, OperationFailedException {
        return super.save(entityInstance);
    }
}
