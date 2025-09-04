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
    // Setters
    private GoalPeriod goalPeriod;
    private Date startDate;
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

    // Business logic methods
    public BigDecimal calculateRollupProgress() {
        if (departmentGoals == null || departmentGoals.isEmpty()) {
            return this.getProgress();
        }

        BigDecimal totalContribution = BigDecimal.ZERO;
        BigDecimal weightedProgress = BigDecimal.ZERO;

        for (DepartmentGoal deptGoal : departmentGoals) {
            if (deptGoal.getIsActive() && deptGoal.getContributionToParent() != null) {
                BigDecimal contribution = deptGoal.getContributionToParent();
                BigDecimal deptProgress = deptGoal.getProgress();

                totalContribution = totalContribution.add(contribution);
                weightedProgress = weightedProgress.add(
                        contribution.multiply(deptProgress).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP));
            }
        }

        // If total contribution is 100%, return weighted progress
        if (totalContribution.compareTo(new BigDecimal("100")) == 0) {
            return weightedProgress.setScale(2, BigDecimal.ROUND_HALF_UP);
        }

        // Otherwise, return current progress
        return this.getProgress();
    }

    public boolean validateDepartmentGoalsContribution() {
        if (departmentGoals == null || departmentGoals.isEmpty()) {
            return true;
        }

        BigDecimal totalContribution = BigDecimal.ZERO;
        for (DepartmentGoal deptGoal : departmentGoals) {
            if (deptGoal.getIsActive() && deptGoal.getContributionToParent() != null) {
                totalContribution = totalContribution.add(deptGoal.getContributionToParent());
            }
        }

        return totalContribution.compareTo(new BigDecimal("100")) == 0;
    }

    // Getters and Setters
    @NotNull(message = "Goal period is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_period_id", nullable = false)
    public GoalPeriod getGoalPeriod() {
        return goalPeriod;
    }

    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    public Date getStartDate() {
        return startDate;
    }

    // One-to-many relationship with DepartmentGoals
    @OneToMany(mappedBy = "parentGoal", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<DepartmentGoal> getDepartmentGoals() {
        return departmentGoals;
    }

}
