package org.pahappa.systems.kpiTracker.models.goalMgt;

import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import org.pahappa.systems.kpiTracker.constants.GoalLevel;

@Setter
@Entity
@Table(name = "department_goals")
public class DepartmentGoal extends BaseGoal {

    private static final long serialVersionUID = 1L;
    // Setters
    private OrganisationGoal parentGoal;
    private String departmentName;
    private List<TeamGoal> teamGoals;

    public DepartmentGoal() {
        super();
        this.setGoalLevel(GoalLevel.DEPARTMENT);
    }

    @Override
    public String goalType() {
        return "Department Goal";
    }

    @Override
    public String parentGoalReference() {
        return parentGoal != null ? parentGoal.getTitle() : "N/A";
    }

    @Override
    public boolean hasChildren() {
        return true;
    }

    @Override
    public boolean canBeParentOf(GoalLevel childLevel) {
        return childLevel == GoalLevel.TEAM;
    }

    // Business logic methods
    public BigDecimal calculateRollupProgress() {
        if (teamGoals == null || teamGoals.isEmpty()) {
            return this.getProgress();
        }

        BigDecimal totalContribution = BigDecimal.ZERO;
        BigDecimal weightedProgress = BigDecimal.ZERO;

        for (TeamGoal teamGoal : teamGoals) {
            if (teamGoal.getIsActive() && teamGoal.getContributionToParent() != null) {
                BigDecimal contribution = teamGoal.getContributionToParent();
                BigDecimal teamProgress = teamGoal.getProgress();

                totalContribution = totalContribution.add(contribution);
                weightedProgress = weightedProgress.add(
                        contribution.multiply(teamProgress).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP));
            }
        }

        // If total contribution is 100%, return weighted progress
        if (totalContribution.compareTo(new BigDecimal("100")) == 0) {
            return weightedProgress.setScale(2, BigDecimal.ROUND_HALF_UP);
        }

        // Otherwise, return current progress
        return this.getProgress();
    }

    public boolean validateTeamGoalsContribution() {
        if (teamGoals == null || teamGoals.isEmpty()) {
            return true;
        }

        BigDecimal totalContribution = BigDecimal.ZERO;
        for (TeamGoal teamGoal : teamGoals) {
            if (teamGoal.getIsActive() && teamGoal.getContributionToParent() != null) {
                totalContribution = totalContribution.add(teamGoal.getContributionToParent());
            }
        }

        return totalContribution.compareTo(new BigDecimal("100")) == 0;
    }

//    public boolean validateEndDate() {
//        if (parentGoal == null || parentGoal.getEndDate() == null) {
//            return true;
//        }
//        return this.getEndDate() == null || !this.getEndDate().after(parentGoal.getEndDate());
//    }

    // Getters
    @NotNull(message = "Parent organisation goal is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_goal_id", nullable = false)
    public OrganisationGoal getParentGoal() {
        return parentGoal;
    }

    @NotNull(message = "Department name is required")
    @Size(max = 100, message = "Department name cannot exceed 100 characters")
    @Column(name = "department_name", nullable = false)
    public String getDepartmentName() {
        return departmentName;
    }

    // One-to-many relationship with TeamGoals
    @OneToMany(mappedBy = "parentGoal", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<TeamGoal> getTeamGoals() {
        return teamGoals;
    }

}
