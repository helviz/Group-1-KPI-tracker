package org.pahappa.systems.kpiTracker.models.goalMgt;

import lombok.Setter;
import org.pahappa.systems.kpiTracker.models.activity.Activity;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.models.kpi.KPI;
import org.pahappa.systems.kpiTracker.models.team.Team;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import org.pahappa.systems.kpiTracker.constants.GoalLevel;

@Setter
@Entity
@Table(name = "individual_goals")
public class IndividualGoal extends BaseGoal {

    private static final long serialVersionUID = 1L;

    // Setters
    private TeamGoal parentGoal;
    private String ownerName;
    private Department department;
    private Team team;
    private List<Activity> activities;
    private List<KPI> kpis;

    public IndividualGoal() {
        super();
        this.setGoalLevel(GoalLevel.INDIVIDUAL);
    }

    @Override
    public String goalType() {
        return "Individual Goal";
    }

    @Override
    public String parentGoalReference() {
        return parentGoal != null ? parentGoal.getTitle() : "N/A";
    }

    @Override
    public boolean hasChildren() {
        return false; // Individual goals are leaf nodes
    }

    @Override
    public boolean canBeParentOf(GoalLevel childLevel) {
        return false; // Individual goals cannot have child goals
    }

    // // Business logic methods
    // public boolean validateEndDate() {
    // if (parentGoal == null || parentGoal.getEndDate() == null) {
    // return true;
    // }
    // return this.getEndDate() == null ||
    // !this.getEndDate().after(parentGoal.getEndDate());
    // }

    public void updateOwnerName() {
        if (this.getOwner() != null) {
            this.ownerName = this.getOwner().getFirstName() + " " + this.getOwner().getLastName();
        }
    }

    public void updateDepartmentAndTeam() {
        if (this.getOwner() != null) {
            // This would typically be set when the goal is created or updated
            // based on the owner's current department and team assignments
        }
    }

    // Getters and Setters
    @NotNull(message = "Parent team goal is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_goal_id", nullable = false)
    public TeamGoal getParentGoal() {
        return parentGoal;
    }

    @Size(max = 100, message = "Owner name cannot exceed 100 characters")
    @Column(name = "owner_name")
    public String getOwnerName() {
        return ownerName;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    public Department getDepartment() {
        return department;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    public Team getTeam() {
        return team;
    }

    // One-to-many relationships with Activities and KPIs
    @OneToMany(mappedBy = "goal", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<Activity> getActivities() {
        return activities;
    }

    @OneToMany(mappedBy = "goal", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<KPI> getKpis() {
        return kpis;
    }

}
