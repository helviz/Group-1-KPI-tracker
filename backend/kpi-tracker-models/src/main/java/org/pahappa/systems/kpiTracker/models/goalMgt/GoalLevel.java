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



}
