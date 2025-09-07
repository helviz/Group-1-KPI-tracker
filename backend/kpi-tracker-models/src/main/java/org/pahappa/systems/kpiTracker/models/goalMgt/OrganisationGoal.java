package org.pahappa.systems.kpiTracker.models.goalMgt;

import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.pahappa.systems.kpiTracker.constants.GoalLevel;

@Setter
@Entity
@Table(name = "organisation_goals")
public class OrganisationGoal extends BaseGoal {

    private static final long serialVersionUID = 1L;
    private GoalPeriod goalPeriod;
    private List<DepartmentGoal> departmentGoals;

    public OrganisationGoal() {
        super();
        this.setGoalLevel(GoalLevel.ORGANISATION);
        this.setContributionToParent(new BigDecimal("100.0")); // Organisation goals contribute 100% to themselves
    }

    @Override
    public String goalType() {
        return "Organisation Goal";
    }

    @Override
    public String parentGoalReference() {
        return "N/A - Top Level Goal";
    }

    @Override
    public boolean hasChildren() {
        return true;
    }

    @Override
    public boolean canBeParentOf(GoalLevel childLevel) {
        return childLevel == GoalLevel.DEPARTMENT;
    }

    // Getters
    @NotNull(message = "Goal period is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "goal_period_id", nullable = false)
    public GoalPeriod getGoalPeriod() {
        return goalPeriod;
    }

    // Organisation goals inherit their date period from the selected goal period
    @Transient
    public Date getStartDate() {
        try {
            return goalPeriod != null ? goalPeriod.getStartDate() : null;
        } catch (Exception e) {
            // Handle lazy initialization exception
            return null;
        }
    }

    @Transient
    public Date getEndDate() {
        try {
            return goalPeriod != null ? goalPeriod.getEndDate() : null;
        } catch (Exception e) {
            // Handle lazy initialization exception
            return null;
        }
    }

    // One-to-many relationship with DepartmentGoals
    @OneToMany(mappedBy = "parentGoal", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public List<DepartmentGoal> getDepartmentGoals() {
        try {
            return departmentGoals;
        } catch (Exception e) {
            // Handle lazy initialization exception
            return null;
        }
    }

    // Business logic methods
    public BigDecimal calculateRollupProgress() {
        List<DepartmentGoal> deptGoals = getDepartmentGoals();
        if (deptGoals == null || deptGoals.isEmpty()) {
            return this.getProgress();
        }

        BigDecimal totalContribution = BigDecimal.ZERO;
        BigDecimal weightedProgress = BigDecimal.ZERO;

        for (DepartmentGoal deptGoal : deptGoals) {
            if (deptGoal.getIsActive() && deptGoal.getContributionToParent() != null) {
                BigDecimal contribution = deptGoal.getContributionToParent();
                BigDecimal deptProgress = deptGoal.getProgress();

                totalContribution = totalContribution.add(contribution);
                weightedProgress = weightedProgress.add(
                        contribution.multiply(deptProgress).divide(new BigDecimal("100"), 2,
                                java.math.RoundingMode.HALF_UP));
            }
        }

        // If total contribution is 100%, return weighted progress
        if (totalContribution.compareTo(new BigDecimal("100")) == 0) {
            return weightedProgress.setScale(2, java.math.RoundingMode.HALF_UP);
        }

        // Otherwise, return current progress
        return this.getProgress();
    }

    public boolean validateDepartmentGoalsContribution() {
        List<DepartmentGoal> deptGoals = getDepartmentGoals();
        if (deptGoals == null || deptGoals.isEmpty()) {
            return true;
        }

        BigDecimal totalContribution = BigDecimal.ZERO;
        for (DepartmentGoal deptGoal : deptGoals) {
            if (deptGoal.getIsActive() && deptGoal.getContributionToParent() != null) {
                totalContribution = totalContribution.add(deptGoal.getContributionToParent());
            }
        }

        return totalContribution.compareTo(new BigDecimal("100")) == 0;
    }

    @Transient
    public boolean isValidEndDate(Date parentEndDate) {
        if (parentEndDate == null)
            return true;
        Date goalEndDate = getEndDate(); // Use the getter method which gets date from goal period
        return goalEndDate == null || !goalEndDate.after(parentEndDate);
    }

}
