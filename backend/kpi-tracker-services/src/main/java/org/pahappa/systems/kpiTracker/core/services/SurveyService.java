package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.models.organisationFit.SurveyCategory;
import org.pahappa.systems.kpiTracker.models.organisationFit.SurveyQuestion;

import java.util.List;

public interface SurveyService {

    // Category operations
    SurveyCategory saveSurveyCategory(SurveyCategory category);
    SurveyCategory getSurveyCategoryById(String id);
    List<SurveyCategory> getAllSurveyCategories();
    void deleteSurveyCategory(SurveyCategory category);

    // Question operations
    SurveyQuestion saveSurveyQuestion(SurveyQuestion question, String categoryId);
    SurveyQuestion getSurveyQuestionById(String id);
    List<SurveyQuestion> getQuestionsByCategory(String categoryId);
    void deleteSurveyQuestion(SurveyQuestion question);
    float getQuestionWeight(String questionId);

    // Combined operations
    SurveyCategory createSurveyWithQuestions(SurveyCategory category, List<SurveyQuestion> questions);
}
