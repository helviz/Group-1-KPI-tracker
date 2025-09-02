package org.pahappa.systems.kpiTracker.models.goalMgt;

import org.sers.webutils.model.BaseEntity;
import org.sers.webutils.model.security.User;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import org.pahappa.systems.kpiTracker.models.goalMgt.GoalLevel;

@MappedSuperclass
public abstract class BaseGoal extends BaseEntity {

    private static final long serialVersionUID = 1L;

    // @NotBlank(message = "Goal title is required")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    @Column(name = "end_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @DecimalMin(value = "0.0", message = "Progress cannot be negative")
    @DecimalMax(value = "100.0", message = "Progress cannot exceed 100%")
    @Column(name = "progress", precision = 5, scale = 2)
    private BigDecimal progress = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Contribution to parent cannot be negative")
    @DecimalMax(value = "100.0", message = "Contribution to parent cannot exceed 100%")
    @Column(name = "contribution_to_parent", precision = 5, scale = 2)
    private BigDecimal contributionToParent = BigDecimal.ZERO;

    @NotNull(message = "Evaluation target is required")
    @DecimalMin(value = "0.0", message = "Evaluation target cannot be negative")
    @Column(name = "evaluation_target", precision = 5, scale = 2, nullable = false)
    private BigDecimal evaluationTarget = new BigDecimal("100.0");

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "goal_level", nullable = false)
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
    public boolean isValidEndDate(Date parentEndDate) {
        if (parentEndDate == null)
            return true;
        return this.endDate == null || !this.endDate.after(parentEndDate);
    }

    @Transient
    public boolean isValidContribution() {
        return this.contributionToParent != null &&
                this.contributionToParent.compareTo(BigDecimal.ZERO) >= 0 &&
                this.contributionToParent.compareTo(new BigDecimal("100")) <= 0;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getProgress() {
        return calculateProgress();
    }

    public void setProgress(BigDecimal progress) {
        updateProgress(progress);
    }

    public BigDecimal getContributionToParent() {
        return contributionToParent;
    }

    public void setContributionToParent(BigDecimal contributionToParent) {
        this.contributionToParent = contributionToParent;
    }

    public BigDecimal getEvaluationTarget() {
        return evaluationTarget;
    }

    public void setEvaluationTarget(BigDecimal evaluationTarget) {
        this.evaluationTarget = evaluationTarget;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public GoalLevel getGoalLevel() {
        return goalLevel;
    }

    public void setGoalLevel(GoalLevel goalLevel) {
        this.goalLevel = goalLevel;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
