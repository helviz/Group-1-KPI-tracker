package org.pahappa.systems.kpiTracker.models.goalMgt;

import lombok.Setter;
import org.pahappa.systems.kpiTracker.constants.Approvals;
import org.pahappa.systems.kpiTracker.constants.GoalStatus;
import org.pahappa.systems.kpiTracker.models.activity.Activity;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.models.kpi.KPI;
import org.pahappa.systems.kpiTracker.models.team.Team;
import org.sers.webutils.model.BaseEntity;
import org.sers.webutils.model.security.User;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import javax.persistence.*;

@Entity
@Setter
@Table(name = "goals")
public class Goal extends BaseEntity {
    private String goalTitle;
    private String Description;
    private User owner;
    private GoalStatus goalStatus;
    private GoalPeriod goalPeriod;
    private GoalLevel goalLevel;
    private Approvals approvalStatus;
    private User approveBy;
    private Double goalEvaluationWeight;
    private Double progress = 0.0;
    private List<Activity> activities = new ArrayList<>();
    private List<GoalDepartment> goalDepartments = new ArrayList<>();
    private Goal parentGoal;
    private Set<Goal> childGoals = new HashSet<>();
    private Team team;

    // New relationship with KPIs for Individual goals
    private Set<KPI> kpis = new HashSet<>();

    // New relationship with Activities for Individual goals
    private Set<Activity> activities = new HashSet<>();

    @Column(name = "goal_title", nullable = false)
    public String getGoalTitle() {
        return goalTitle;
    }

    @Column(name = "description", columnDefinition = "TEXT")
    public String getDescription() {
        return Description;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    public User getOwner() {
        return owner;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "goal_status")
    public GoalStatus getGoalStatus() {
        return goalStatus;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_period_id", nullable = false)
    public GoalPeriod getGoalPeriod() {
        return goalPeriod;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_level_id", nullable = false)
    public GoalLevel getGoalLevel() {
        return goalLevel;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status")
    public Approvals getApprovalStatus() {
        return approvalStatus;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_user_id")
    public User getApproveBy() {
        return approveBy;
    }

    @Column(name = "goal_evaluation_weight")
    public Double getGoalEvaluationWeight() {
        return goalEvaluationWeight;
    }

    @Column(name = "goal_progress")
    public Double getProgress() {
        return progress;
    }

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<GoalDepartment> getGoalDepartments() {
        return goalDepartments;
    }

    @OneToMany(
            mappedBy = "goal", 
            cascade = CascadeType.ALL, 
            orphanRemoval = true 
    )
    public List<GoalDepartment> getGoalDepartments(){
    return goalDepartments;
}

    @OneToMany(
            mappedBy = "goal",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    public List<Activity> getActivities() {
        return activities;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_goal_id")
    public Goal getParentGoal() {
        return parentGoal;
    }

    @OneToMany(mappedBy = "parentGoal", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<Goal> getChildGoals() {
        return childGoals;
    }

    // New relationship with KPIs for Individual goals
    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public Set<KPI> getKpis() {
        return kpis;
    }




    // Add this new getter method
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id") // This will create a 'team_id' column in your 'goals' table
    public Team getTeam() {
        return team;
    }

    // New relationship with Activities for Individual goals
    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public Set<Activity> getActivities() {
        return activities;
    }

    public void addDepartment(Department department, Double weight) {
        GoalDepartment goalDepartment = new GoalDepartment(this, department, weight);
        this.goalDepartments.add(goalDepartment);
    }

    public void addKpi(KPI kpi) {
        this.kpis.add(kpi);
        kpi.setGoal(this);
    }

    public void removeKpi(KPI kpi) {
        this.kpis.remove(kpi);
        kpi.setGoal(null);
    }

    public void addActivity(Activity activity) {
        this.activities.add(activity);
        activity.setGoal(this);
    }

    public void removeActivity(Activity activity) {
        this.activities.remove(activity);
        activity.setGoal(null);
    }

    @PrePersist
    @PreUpdate
    protected void onSave() {
        // Set default status based on hierarchy
        if (this.goalStatus == null) {
            if (this.parentGoal == null) {
                // Organization level goals start as ON_TRACK
                this.goalStatus = GoalStatus.ON_TRACK;
            } else {
                // All other goals start as PENDING
                this.goalStatus = GoalStatus.PENDING;
            }
        }

        // Set default approval status if not set
        if (this.approvalStatus == null) {
            this.approvalStatus = Approvals.PENDING;
        }

        // Set default progress if not set
        if (this.progress == null) {
            this.progress = 0.0;
        }

        // Set default evaluation weight if not set
        if (this.goalEvaluationWeight == null) {
            this.goalEvaluationWeight = 0.0;
        }
    }

    /**
     * Check if this goal is at the Individual level
     */
    @Transient
    public boolean isIndividualGoal() {
        return this.goalLevel != null && "Individual".equalsIgnoreCase(this.goalLevel.getName());
    }

    /**
     * Check if this goal is at the Organization level
     */
    @Transient
    public boolean isOrganizationGoal() {
        return this.goalLevel != null && "Organization".equalsIgnoreCase(this.goalLevel.getName());
    }

    /**
     * Check if this goal is at the Department level
     */
    @Transient
    public boolean isDepartmentGoal() {
        return this.goalLevel != null && "Department".equalsIgnoreCase(this.goalLevel.getName());
    }

    /**
     * Check if this goal is at the Team level
     */
    @Transient
    public boolean isTeamGoal() {
        return this.goalLevel != null && "Team".equalsIgnoreCase(this.goalLevel.getName());
    }
}
