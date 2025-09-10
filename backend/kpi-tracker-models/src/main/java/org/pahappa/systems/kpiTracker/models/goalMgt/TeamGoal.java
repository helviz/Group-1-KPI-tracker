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
@Table(name = "team_goals")
public class TeamGoal extends BaseGoal {

    private static final long serialVersionUID = 1L;

    // Setters
    private DepartmentGoal parentGoal;
    private String teamName;
    private List<IndividualGoal> individualGoals;

    public TeamGoal() {
        super();
        this.setGoalLevel(GoalLevel.TEAM);
    }

    @Override
    public String goalType() {
        return "Team Goal";
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
        return childLevel == GoalLevel.INDIVIDUAL;
    }

    // Business logic methods
    public BigDecimal calculateRollupProgress() {
        if (individualGoals == null || individualGoals.isEmpty()) {
            return this.getProgress();
        }

        BigDecimal totalContribution = BigDecimal.ZERO;
        BigDecimal weightedProgress = BigDecimal.ZERO;

        for (IndividualGoal individualGoal : individualGoals) {
            if (individualGoal.getIsActive() && individualGoal.getContributionToParent() != null) {
                BigDecimal contribution = individualGoal.getContributionToParent();
                BigDecimal individualProgress = individualGoal.getProgress();

                totalContribution = totalContribution.add(contribution);
                weightedProgress = weightedProgress.add(
                        contribution.multiply(individualProgress).divide(new BigDecimal("100"), 2,
                                BigDecimal.ROUND_HALF_UP));
            }
        }

        // If total contribution is 100%, return weighted progress
        if (totalContribution.compareTo(new BigDecimal("100")) == 0) {
            return weightedProgress.setScale(2, BigDecimal.ROUND_HALF_UP);
        }

        // Otherwise, return current progress
        return this.getProgress();
    }

    public boolean validateIndividualGoalsContribution() {
        if (individualGoals == null || individualGoals.isEmpty()) {
            return true;
        }

        BigDecimal totalContribution = BigDecimal.ZERO;
        for (IndividualGoal individualGoal : individualGoals) {
            if (individualGoal.getIsActive() && individualGoal.getContributionToParent() != null) {
                totalContribution = totalContribution.add(individualGoal.getContributionToParent());
            }
        }

        return totalContribution.compareTo(new BigDecimal("100")) == 0;
    }

    // Getters
    @NotNull(message = "Parent department goal is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_goal_id", nullable = false)
    public DepartmentGoal getParentGoal() {
        return parentGoal;
    }

    @NotNull(message = "Team name is required")
    @Size(max = 100, message = "Team name cannot exceed 100 characters")
    @Column(name = "team_name", nullable = false)
    public String getTeamName() {
        return teamName;
    }

    @OneToMany(mappedBy = "parentGoal", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<IndividualGoal> getIndividualGoals() {
        return individualGoals;
    }

}
