package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.services.GoalPeriodService;
import org.pahappa.systems.kpiTracker.models.goalMgt.GoalPeriod;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GoalPeriodServiceImpl extends GenericServiceImpl<GoalPeriod> implements GoalPeriodService {
    @Override
    public boolean isDeletable(GoalPeriod instance) throws OperationFailedException {
        return true;
    }

    @Override
    public GoalPeriod saveInstance(GoalPeriod entityInstance) throws ValidationFailedException, OperationFailedException {
            return super.save(entityInstance);
    }
}
