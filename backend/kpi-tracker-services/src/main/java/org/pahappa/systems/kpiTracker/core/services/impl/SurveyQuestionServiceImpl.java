package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.services.SurveyQuestionService;
import org.pahappa.systems.kpiTracker.models.organisationFit.SurveyQuestion;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class SurveyQuestionServiceImpl extends GenericServiceImpl<SurveyQuestion> implements SurveyQuestionService {


    @Override
    public boolean isDeletable(SurveyQuestion instance) throws OperationFailedException {
        return true;
    }

    @Override
    public SurveyQuestion saveInstance(SurveyQuestion entityInstance) throws ValidationFailedException, OperationFailedException {
        Validate.hasText(entityInstance.getQuestion(), "Question name is required");
        Validate.notNull(entityInstance.getCategory(), "Question category is required");
        return super.save(entityInstance);
    }


}
