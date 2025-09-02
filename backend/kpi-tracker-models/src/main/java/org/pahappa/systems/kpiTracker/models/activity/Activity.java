package org.pahappa.systems.kpiTracker.models.activity;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.constants.ActivityPriority;
import org.pahappa.systems.kpiTracker.constants.ActivityStatus;
import org.pahappa.systems.kpiTracker.models.goalMgt.IndividualGoal;
import org.sers.webutils.model.BaseEntity;
import org.sers.webutils.model.security.User;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Setter
@Entity
@Table(name = "activities")
public class Activity extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @Column(name = "activity_name", nullable = false)
    private String name;

    @Column(name = "activity_description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_status", nullable = false)
    private ActivityStatus status = ActivityStatus.NOT_STARTED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = true)
    private IndividualGoal goal;

    public String getName() {
        return name;
    }



    public String getDescription() {
        return description;
    }



    public ActivityStatus getStatus() {
        return status;
    }


    public IndividualGoal getGoal() {
        return goal;
    }
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;


    @Column(name = "priority")
    @Enumerated(EnumType.STRING)
    private ActivityPriority priority = ActivityPriority.MEDIUM;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        Activity activity = (Activity) o;
        return Objects.equals(name, activity.name) &&
                Objects.equals(description, activity.description) &&
                status == activity.status &&
                Objects.equals(goal, activity.goal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, description, status, goal);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
