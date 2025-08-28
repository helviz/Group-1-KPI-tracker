package org.pahappa.systems.kpiTracker.models.activity;

import org.pahappa.systems.kpiTracker.constants.ActivityStatus;
import org.pahappa.systems.kpiTracker.models.goalMgt.Goal;
import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "activities")
public class Activity extends BaseEntity {

    @Column(name = "activity_name", nullable = false)
    private String name;

    @Column(name = "activity_description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_status", nullable = false)
    private ActivityStatus status = ActivityStatus.NOT_STARTED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    private Goal goal;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ActivityStatus getStatus() {
        return status;
    }

    public void setStatus(ActivityStatus status) {
        this.status = status;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
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
