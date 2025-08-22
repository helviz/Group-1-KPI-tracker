package org.pahappa.systems.kpiTracker.views.OrganisationFit;

import org.pahappa.systems.kpiTracker.core.services.SurveyCategoryService;
import org.pahappa.systems.kpiTracker.core.services.SurveyQuestionService;
import org.pahappa.systems.kpiTracker.models.organisationFit.SurveyCategory;
import org.pahappa.systems.kpiTracker.models.organisationFit.SurveyQuestion;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "surveyQuestionFormDialog", eager = true)
@SessionScoped
@ViewPath(path = HyperLinks.SURVEY_QUESTION_FORM_DIALOG)
public class SurveyQuestionFormDialog extends DialogForm<SurveyQuestion> {

    private static final long serialVersionUID = 1L;

    private SurveyQuestionService questionService;
    private SurveyCategoryService categoryService;

    private List<SurveyQuestion> questions = new ArrayList<>();
    private SurveyCategory selectedCategory;
    private boolean edit;



    public SurveyQuestionFormDialog() {
        super(HyperLinks.SURVEY_QUESTION_FORM_DIALOG, 700, 600);
    }

    public SurveyQuestionFormDialog(String name, int width, int height) {
        super(name, width, height);
    }

    @PostConstruct
    public void init() {
        this.questionService = ApplicationContextProvider.getBean(SurveyQuestionService.class);
        this.categoryService = ApplicationContextProvider.getBean(SurveyCategoryService.class);


        if (super.model == null) {
            prepareNewQuestion();
        }
        loadQuestionsForCategory();
    }

    /**
     * Load questions for the current selected category
     */
    public void loadQuestionsForCategory() {
        if (!isCategorySelected()) {
            this.questions = new ArrayList<>();
            return;
        }
        try {
            this.selectedCategory = categoryService.getInstanceByID(this.selectedCategory.getId());
            this.questions = this.selectedCategory.getSurveyQuestions();
            if (this.questions == null) {
                this.questions = new ArrayList<>();
            }
        } catch (Exception e) {
            addErrorMessage("Error loading questions: " + e.getMessage());
            this.questions = new ArrayList<>();
        }
    }

    @Override
    public void show(ActionEvent actionEvent) {
        if (!ensureCategorySelected()) {
            return;
        }
        if (super.model == null) {
            prepareNewQuestion();
        }
        loadQuestionsForCategory();
        super.show(actionEvent);
    }

    @Override
    public void persist() throws Exception {
        return;
    }


    public void persistQuestion(String questionText) throws Exception {
        if (!ensureCategorySelected()) return;

        SurveyQuestion question = new SurveyQuestion();
        question.setQuestion(questionText);
        question.setCategory(selectedCategory);

        selectedCategory.getSurveyQuestions().add(question);
        categoryService.saveInstance(selectedCategory);

        loadQuestionsForCategory();
        addSuccessMessage("Question added successfully!");
        prepareNewQuestion();
        
    }


    /**
     * Remove a question from the category
     */
    public void removeQuestion(SurveyQuestion question) {
        try {
            if (question != null && question.getId() != null) {
                // Remove from the in-memory list
                selectedCategory.getSurveyQuestions().remove(question);

                // Save the category to trigger orphanRemoval
                categoryService.saveInstance(selectedCategory);

                // Reload questions list to refresh UI
                loadQuestionsForCategory();

                addSuccessMessage("Question removed successfully!");
            }
        } catch (Exception e) {
            addErrorMessage("Error removing question: " + e.getMessage());
        }
    }



    @Override
    public void resetModal() {
        super.resetModal();
        prepareNewQuestion();
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        if (super.model != null && super.model.getId() != null) {
            setEdit(true);
        } else {
            prepareNewQuestion();
        }
    }

    /**
     * Show dialog using the pre-set selectedCategory
     */
    public void showForCategory(ActionEvent actionEvent) {
        if (!ensureCategorySelected()) {
            return;
        }
        prepareNewQuestion();
        loadQuestionsForCategory();
        super.show(actionEvent);
    }

    // ---------------- Utility Methods ----------------

    private void prepareNewQuestion() {
        super.model = new SurveyQuestion();
        if (this.selectedCategory != null) {
            super.model.setCategory(this.selectedCategory);
        }
        setEdit(false);
    }

    private boolean ensureCategorySelected() {
        if (this.selectedCategory == null) {
            addErrorMessage("No category selected");
            return false;
        }
        return true;
    }

    private void addSuccessMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", message));
    }

    private void addErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", message));
    }

    // ---------------- Getters and Setters ----------------

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public SurveyCategory getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(SurveyCategory selectedCategory) {
        this.selectedCategory = selectedCategory;
        loadQuestionsForCategory();
    }

    public List<SurveyQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<SurveyQuestion> questions) {
        this.questions = questions;
    }

    public SurveyQuestionService getQuestionService() {
        return questionService;
    }

    public SurveyCategoryService getCategoryService() {
        return categoryService;
    }

    public boolean isCategorySelected() {
        return this.selectedCategory != null;
    }

    public int getQuestionCount() {
        return questions != null ? questions.size() : 0;
    }
}
