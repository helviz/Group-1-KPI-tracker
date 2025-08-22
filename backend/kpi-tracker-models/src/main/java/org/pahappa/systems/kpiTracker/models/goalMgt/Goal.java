package org.pahappa.systems.kpiTracker.models.goalMgt;

import lombok.Setter;
import org.pahappa.systems.kpiTracker.constants.Approvals;
import org.pahappa.systems.kpiTracker.constants.GoalStatus;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.sers.webutils.model.BaseEntity;
import org.sers.webutils.model.security.User;
import java.util.List; // Make sure to import List
import java.util.ArrayList; // And ArrayList

import javax.persistence.*;

@Entity
@Setter
@Table(name = "goal")
public class Goal extends BaseEntity {
    private  String goalTitle;
    private String Description;
    private User  ownerId;
    private GoalStatus goalStatus;//enum
    private GoalPeriod goalPeriod; //table
    private GoalLevel goalLevelId;//table
    private Approvals approvalStatus; //enum
    private  User approveByUserId;
    private Department department;
    private  Double goalEvaluationWeight;
    private Double progress = 0.0;


    @Column(name = "goal_title", nullable = false)
    public String getGoalTitle() {
        return goalTitle;
    }
    @Column(name = "description", columnDefinition = "TEXT") // Use TEXT for potentially long descriptions
    public String getDescription() {
        return Description;
    }

    @ManyToOne(fetch = FetchType.LAZY) // Many goals can belong to one owner
    @JoinColumn(name = "owner_id")
    public User getOwnerId() {
        return ownerId;
    }
    @Enumerated(EnumType.STRING) // Store the enum as a readable string (e.g., "PENDING") in the DB
    @Column(name = "goal_status")
    public GoalStatus getGoalStatus() {
        return goalStatus;
    }

    @ManyToOne(fetch = FetchType.LAZY) // Many goals can belong to one period
    @JoinColumn(name = "goal_period_id")
    public GoalPeriod getGoalPeriod() {
        return goalPeriod;
    }

    @ManyToOne(fetch = FetchType.LAZY) // Many goals can have one level
    @JoinColumn(name = "goal_level_id")
    public GoalLevel getGoalLevelId() {
        return goalLevelId;
    }
    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status")
    public Approvals getApprovalStatus() {
        return approvalStatus;
    }

    @ManyToOne(fetch = FetchType.LAZY) // Many goals can be approved by one user
    @JoinColumn(name = "approved_by_user_id")
    public User getApproveByUserId() {
        return approveByUserId;
    }

    @ManyToOne(fetch = FetchType.LAZY) // Many goals can belong to one department
    @JoinColumn(name = "department_id")
    public Department getDepartment() {
        return department;
    }


    @Column(name = "goal_evaluation_weight")
    public Double getGoalEvaluationWeight() {
        return goalEvaluationWeight;
    }

    @Column(name = "goal_progress" )
   public Double getProgress( ){
     return progress;
    }// Initialize to 0 by default

}
