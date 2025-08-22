package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.models.organisationFit.SurveyCategory;

import java.util.List;

public interface SurveyCategoryService extends GenericService<SurveyCategory> {
    List<SurveyCategory> getAllInstances();
}
