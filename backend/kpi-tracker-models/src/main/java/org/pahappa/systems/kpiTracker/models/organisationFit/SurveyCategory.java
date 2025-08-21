package org.pahappa.systems.kpiTracker.models.organisationFit;

import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(
        name = "survey_category"
)
public class SurveyCategory extends BaseEntity {
    private String name;
    private String description;
    private float weight;
    private List<SurveyQuestion> surveyQuestions;


//    getters and setters
    @Column(nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Column(length=1000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(nullable = false)
    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    @OneToMany(
            mappedBy = "category",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    public List<SurveyQuestion> getSurveyQuestions() {
        return surveyQuestions;
    }

    public void setSurveyQuestions(List<SurveyQuestion> surveyQuestions) {
        this.surveyQuestions = surveyQuestions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SurveyCategory)) return false;

        SurveyCategory that = (SurveyCategory) o;

        // If both have IDs, compare by ID
        if (this.getId() != null && that.getId() != null) {
            return this.getId().equals(that.getId());
        }

        // Otherwise, compare by business key(s)
        return name != null && name.equals(that.getName());
    }

    @Override
    public int hashCode() {
        // Use ID if available, otherwise fallback
        return getId() != null ? getId().hashCode() : (name != null ? name.hashCode() : 0);
    }

    @Override
    public String toString() {
        return "SurveyCategory{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", weight=" + weight +
                ", recordStatus=" + getRecordStatus() +
                '}';
    }

    @Transient
    public int getSurveyQuestionsCount() {
        return surveyQuestions == null ? 0 : surveyQuestions.size();
    }


}
