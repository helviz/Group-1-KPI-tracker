package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.services.SurveyCategoryService;
import org.pahappa.systems.kpiTracker.models.organisationFit.SurveyCategory;
import org.pahappa.systems.kpiTracker.models.team.Team;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SurveyCategoryServiceImpl extends GenericServiceImpl<SurveyCategory> implements SurveyCategoryService {

    @Override
    public boolean isDeletable(SurveyCategory instance) throws OperationFailedException {
        return true;
    }

    @Override
    public SurveyCategory saveInstance(SurveyCategory entityInstance) throws ValidationFailedException, OperationFailedException {
        Validate.hasText(entityInstance.getName(), "Category name is required");
        Validate.notNull(entityInstance.getDescription(), "Category description is required");
        Validate.notNull(entityInstance.getWeight(), "Category weight is required");
        return super.save(entityInstance);
    }

    @Override
    public List<SurveyCategory> getAllInstances(){
        return super.getAllInstances();
    }
}
