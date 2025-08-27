package org.pahappa.systems.kpiTracker.models.goalMgt;

import lombok.Getter;
import lombok.Setter;
import org.sers.webutils.model.BaseEntity;
import org.sers.webutils.model.security.User;

import javax.persistence.*;
import java.util.Date;
@Setter

@Entity
@Table(name = "activities")
public class Activity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    private Goal goal;

    // Getters and Setters
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ActivityStatus status = ActivityStatus.PENDING;

    @Column(name = "due_date")
    @Temporal(TemporalType.DATE)
    private Date dueDate;

    @Column(name = "completed_date")
    @Temporal(TemporalType.DATE)
    private Date completedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @Column(name = "priority")
    @Enumerated(EnumType.STRING)
    private ActivityPriority priority = ActivityPriority.MEDIUM;

    public Goal getGoal() {
        return goal;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public ActivityStatus getStatus() {
        return status;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public Date getCompletedDate() {
        return completedDate;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public ActivityPriority getPriority() {
        return priority;
    }
}
