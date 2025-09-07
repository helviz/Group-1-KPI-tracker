package org.pahappa.systems.kpiTracker.models.goalMgt;

import lombok.Setter;
import org.sers.webutils.model.BaseEntity;
import org.sers.webutils.model.security.User;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import org.pahappa.systems.kpiTracker.constants.GoalLevel;
@Setter
@MappedSuperclass
public abstract class BaseGoal extends BaseEntity {

    private static final long serialVersionUID = 1L;


    private String title;

    private String description;




    private BigDecimal progress = BigDecimal.ZERO;


    private BigDecimal contributionToParent = BigDecimal.ZERO;


    private BigDecimal evaluationTarget = new BigDecimal("100.0");


    private User owner;


    private GoalLevel goalLevel;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // Abstract methods that must be implemented by subclasses
    public abstract String goalType();

    public abstract String parentGoalReference();

    public abstract boolean hasChildren();

    public abstract boolean canBeParentOf(GoalLevel childLevel);

    // Progress calculation methods
    public BigDecimal calculateProgress() {
        if (this.progress == null) {
            this.progress = BigDecimal.ZERO;
        }
        return this.progress.setScale(2, RoundingMode.HALF_UP);
    }

    public void updateProgress(BigDecimal newProgress) {
        if (newProgress == null) {
            this.progress = BigDecimal.ZERO;
        } else if (newProgress.compareTo(BigDecimal.ZERO) < 0) {
            this.progress = BigDecimal.ZERO;
        } else if (newProgress.compareTo(new BigDecimal("100")) > 0) {
            this.progress = new BigDecimal("100");
        } else {
            this.progress = newProgress.setScale(2, RoundingMode.HALF_UP);
        }
    }

    // Validation methods


    @Transient
    public boolean isValidContribution() {
        return this.contributionToParent != null &&
                this.contributionToParent.compareTo(BigDecimal.ZERO) >= 0 &&
                this.contributionToParent.compareTo(new BigDecimal("100")) <= 0;
    }

    // Getters
    // @NotBlank(message = "Goal title is required")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    @Column(name = "title", nullable = false)
    public String getTitle() {
        return title;
    }

    @Column(name = "description", columnDefinition = "TEXT")
    public String getDescription() {
        return description;
    }


    @DecimalMin(value = "0.0", message = "Progress cannot be negative")
    @DecimalMax(value = "100.0", message = "Progress cannot exceed 100%")
    @Column(name = "progress", precision = 5, scale = 2)
    public BigDecimal getProgress() {
        return calculateProgress();
    }

    @DecimalMin(value = "0.0", message = "Contribution to parent cannot be negative")
    @DecimalMax(value = "100.0", message = "Contribution to parent cannot exceed 100%")
    @Column(name = "contribution_to_parent", precision = 5, scale = 2)
    public BigDecimal getContributionToParent() {
        return contributionToParent;
    }


    @NotNull(message = "Evaluation target is required")
    @DecimalMin(value = "0.0", message = "Evaluation target cannot be negative")
    @Column(name = "evaluation_target", precision = 5, scale = 2, nullable = false)
    public BigDecimal getEvaluationTarget() {
        return evaluationTarget;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    public User getOwner() {
        return owner;
    }



    @Enumerated(EnumType.STRING)
    @Column(name = "goal_level", nullable = false)
    public GoalLevel getGoalLevel() {
        return goalLevel;
    }


    public Boolean getIsActive() {
        return isActive;
    }


}
