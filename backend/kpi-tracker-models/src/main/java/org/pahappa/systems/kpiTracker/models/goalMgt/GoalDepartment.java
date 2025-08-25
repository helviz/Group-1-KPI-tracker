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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    public Goal getGoal(){
        return goal;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department getDepartment() {
    return department;
    }

    @Column(name = "percentage_weight")
    private Double getPercentageWeight(){
        return percentageWeight; // e.g., 20 for 20%
}

}
