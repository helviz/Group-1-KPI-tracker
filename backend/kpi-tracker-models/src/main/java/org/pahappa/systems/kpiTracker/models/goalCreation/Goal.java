package org.pahappa.systems.kpiTracker.models.goalCreation;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.constants.GoalLevel;
import org.pahappa.systems.kpiTracker.constants.GoalStatus;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.models.goalMgt.GoalPeriod;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "goals", indexes = {
        @Index(name = "idx_goal_level", columnList = "goalLevel"),
        @Index(name = "idx_parent_goal", columnList = "parentGoal_id")
})
public class Goal extends BaseEntity {
    private GoalLevel goalLevel;
    private String goalName;
    private String description;
    private double evaluationTarget;
    private Department department;// New: Reference to department
    private double progress;
    private double weight; // contribution to parent
    private Goal parentGoal;
    private GoalPeriod goalPeriod;
    private Staff owner;
    private GoalStatus goalStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "goal_level", nullable = false)
    public GoalLevel getGoalLevel() {
        return goalLevel;
    }

    @Column(name = "goal_progress")
    public double getProgress() {
        return progress;
    }

    @Column(name = "goal_status")
    public GoalStatus getGoalStatus() {
        return goalStatus;
    }

    @ManyToOne
    @JoinColumn(name = "department_id")
    public Department getDepartment() {
        return department;
    }

    @Column(name = "description", nullable = false)
    public String getDescription() {
        return description;
    }

    @Column(name = "goal_name", nullable = false)
    public String getGoalName() {
        return goalName;
    }

    @Column(name = "evaluation_target", precision = 5, scale = 2)
    public double getEvaluationTarget() {
        return evaluationTarget;
    }

    @Column(name = "weight", nullable = false)
    public double getWeight() {
        return weight;
    }

    @ManyToOne
    @JoinColumn(name = "parent_goal_id")
    public Goal getParentGoal() {
        return parentGoal;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "goal_period_id", nullable = false)
    public GoalPeriod getGoalPeriod() {
        return goalPeriod;
    }

    @Transient
    public Date getStartDate() {
        return goalPeriod != null ? goalPeriod.getStartDate() : null;
    }

    @Transient
    public Date getEndDate() {
        return goalPeriod != null ? goalPeriod.getEndDate() : null;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    public Staff getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return this.goalName;
    }

    @Override
    public int hashCode() {
        // Persisted entities are identified by their ID
        return Objects.hash(super.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Goal goal = (Goal) o;
        return super.getId() != null && Objects.equals(super.getId(), goal.getId());
    }


}
