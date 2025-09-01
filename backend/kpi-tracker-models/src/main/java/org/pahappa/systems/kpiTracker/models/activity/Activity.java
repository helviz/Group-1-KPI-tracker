package org.pahappa.systems.kpiTracker.models.activity;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.constants.ActivityPriority;
import org.pahappa.systems.kpiTracker.constants.ActivityStatus;
import org.pahappa.systems.kpiTracker.models.goalMgt.Goal;
import org.sers.webutils.model.BaseEntity;
import org.sers.webutils.model.security.User;

import javax.persistence.*;
import java.util.Date;
@Getter
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

}
