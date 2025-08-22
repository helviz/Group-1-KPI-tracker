package org.pahappa.systems.kpiTracker.models.goalMgt;

import lombok.Setter;
import org.sers.webutils.model.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Setter
@Table(name = "goal_levels")
public class GoalLevel extends BaseEntity {
    private String name;
    private String description;

    public String getDescription(){
        return description;
    }

    public String getName() {
        return name;
    }


    @Override
    public boolean equals(Object o) {
        // 1. Check if it's the same instance
        if (this == o) return true;

        // 2. Check if the other object is null or of a different class
        if (o == null || getClass() != o.getClass()) return false;

        // 3. Cast the object to a GoalLevel
        GoalLevel that = (GoalLevel) o;

        // 4. Two GoalLevels are equal ONLY if their IDs are equal.
        //    This handles cases where one or both IDs might be null (for new, unsaved entities).
        return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
    }

    @Override
    public int hashCode() {
        // The hashCode must also be based ONLY on the ID.
        // This is a Java requirement: if two objects are equal, their hash codes MUST be the same.
        return getId() != null ? getId().hashCode() : 0;
    }

}
