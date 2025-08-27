package org.pahappa.systems.kpiTracker.models.goalMgt;

import lombok.Setter;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "goal_department")
@Setter
public class GoalDepartment  extends BaseEntity {
    private Goal goal;
    private Department department;
    private Double percentageWeight;

    public GoalDepartment() {

    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    public Goal getGoal(){
        return goal;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    public Department getDepartment() {
    return department;
    }

    @Column(name = "percentage_weight")
    public Double getPercentageWeight(){
        return percentageWeight; // e.g., 20 for 20%
}


    public GoalDepartment(Goal goal, Department department, Double percentageWeight) {
        this.goal = goal;
        this.department = department;
        this.percentageWeight = percentageWeight;
    }
}
