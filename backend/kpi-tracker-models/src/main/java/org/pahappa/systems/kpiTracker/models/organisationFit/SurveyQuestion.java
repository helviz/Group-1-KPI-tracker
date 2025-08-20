package org.pahappa.systems.kpiTracker.models.organisationFit;

import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;

@Entity
@Table(
        name="survey_question"
)
public class SurveyQuestion extends BaseEntity {
    private String question;
    private SurveyCategory category;

//    getters and setters
    @Column(nullable = false, length = 1000)
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    public SurveyCategory getCategory() {
        return category;
    }

    public void setCategory(SurveyCategory category) {
        this.category = category;
    }

}
