package org.pahappa.systems.kpiTracker.models.organisationFit;

import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "survey_question")
public class SurveyQuestion extends BaseEntity {
    private String question;
    private SurveyCategory category;

    // Getters and Setters
    @Column(nullable = false, length = 1000)
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    public SurveyCategory getCategory() {
        return category;
    }

    public void setCategory(SurveyCategory category) {
        this.category = category;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SurveyQuestion)) return false;
        SurveyQuestion that = (SurveyQuestion) o;
        return Objects.equals(getId(), that.getId()) ||
                (Objects.equals(question, that.question) &&
                        Objects.equals(category, that.category));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), question, category);
    }

    // toString
    @Override
    public String toString() {
        return "SurveyQuestion{" +
                "id=" + getId() +
                ", question='" + question + '\'' +
                ", category=" + (category != null ? category.getName() : "null") +
                '}';
    }
}
