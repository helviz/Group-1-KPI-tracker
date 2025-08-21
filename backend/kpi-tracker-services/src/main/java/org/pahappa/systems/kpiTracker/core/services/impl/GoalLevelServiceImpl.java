package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.services.GoalLevelService;
import org.pahappa.systems.kpiTracker.models.goalMgt.GoalLevel;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GoalLevelServiceImpl extends GenericServiceImpl<GoalLevel> implements GoalLevelService {
    @Override
    public boolean isDeletable(GoalLevel instance) throws OperationFailedException {
        return true;
    }

    @Override
    public GoalLevel saveInstance(GoalLevel entityInstance) throws ValidationFailedException, OperationFailedException {
        return super.save(entityInstance);
    }
}
