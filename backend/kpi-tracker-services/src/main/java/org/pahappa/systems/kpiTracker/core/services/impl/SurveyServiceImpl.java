package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.dao.SurveyCategoryDao;
import org.pahappa.systems.kpiTracker.core.dao.SurveyQuestionDao;
import org.pahappa.systems.kpiTracker.core.services.SurveyService;
import org.pahappa.systems.kpiTracker.models.organisationFit.SurveyCategory;
import org.pahappa.systems.kpiTracker.models.organisationFit.SurveyQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class SurveyServiceImpl implements SurveyService {
    @Autowired
    private SurveyCategoryDao surveyCategoryDao;

    @Autowired
    private SurveyQuestionDao surveyQuestionDao;


    @Override
    public SurveyCategory saveSurveyCategory(SurveyCategory category) {
        if (category.getId() == null) {
            surveyCategoryDao.add(category);
        } else {
            surveyCategoryDao.update(category);
        }
        return category;
    }

    @Override
    public SurveyCategory getSurveyCategoryById(String id) {
        return surveyCategoryDao.find(id);
    }

    @Override
    public List<SurveyCategory> getAllSurveyCategories() {
        List<SurveyCategory> categories = surveyCategoryDao.findAll();
        return categories != null ? categories : Collections.<SurveyCategory>emptyList();
    }

    @Override
    public void deleteSurveyCategory(SurveyCategory category) {
        if (category != null && category.getId() != null) {
            surveyCategoryDao.delete(category);
        }
    }

    @Override
    public SurveyQuestion saveSurveyQuestion(SurveyQuestion question, String categoryId) {
        SurveyCategory category = surveyCategoryDao.find(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Category with ID " + categoryId + " not found");
        }

        question.setCategory(category);

        if (question.getId() == null) {
            surveyQuestionDao.add(question);
        } else {
            surveyQuestionDao.update(question);
        }

        return question;
    }

    @Override
    public SurveyQuestion getSurveyQuestionById(String id) {
        return surveyQuestionDao.find(id);
    }

    @Override
    public List<SurveyQuestion> getQuestionsByCategory(String categoryId) {
        SurveyCategory category = surveyCategoryDao.find(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Category with ID " + categoryId + " not found");
        }
        List<SurveyQuestion> questions = surveyQuestionDao.searchByPropertyEqual("category", category);
        return questions != null ? questions : Collections.<SurveyQuestion>emptyList();
    }

    @Override
    public void deleteSurveyQuestion(SurveyQuestion question) {
        if (question != null && question.getId() != null) {
            surveyQuestionDao.delete(question);
        }
    }

    @Override
    public float getQuestionWeight(String questionId) {
        // Step 1: Fetch the question by ID
        SurveyQuestion question = surveyQuestionDao.find(questionId);
        if (question == null) {
            throw new IllegalArgumentException("Question with ID " + questionId + " not found");
        }

        // Step 2: Get the category for the question
        SurveyCategory category = question.getCategory();
        if (category == null) {
            throw new IllegalStateException("Question " + questionId + " does not belong to any category");
        }

        // Step 3: Get all questions under this category
        List<SurveyQuestion> questions = surveyQuestionDao.searchByPropertyEqual("category", category);
        if (questions == null || questions.isEmpty()) {
            throw new IllegalStateException("Category " + category.getId() + " has no questions");
        }

        // Step 4: Divide category weight by number of questions
        float categoryWeight = category.getWeight();
        return categoryWeight / questions.size();
    }


    @Override
    public SurveyCategory createSurveyWithQuestions(SurveyCategory category, List<SurveyQuestion> questions) {
        // Save category first
        surveyCategoryDao.add(category);

        // Assign category to each question and save
        for (SurveyQuestion q : questions) {
            q.setCategory(category);
            surveyQuestionDao.add(q);
        }

        // Attach questions to category (optional, if you want the relation updated immediately in memory)
        category.setSurveyQuestions(questions);

        return category;
    }

}
