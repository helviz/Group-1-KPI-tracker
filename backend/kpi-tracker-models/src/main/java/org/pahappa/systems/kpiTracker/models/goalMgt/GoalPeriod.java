package org.pahappa.systems.kpiTracker.models.goalMgt;

import lombok.Setter;
import org.sers.webutils.model.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
@Entity
@Setter
@Table(name = "goal_periods")
public class GoalPeriod extends BaseEntity {
    private String periodName;
    private Date startDate;
    private Date endDate;

    @Column(name = "name", nullable = false)
    public String getperiodName() {
        return periodName;
    }

    @Column(name = "end_date", nullable = false)
    public Date getEndDate() {
        return endDate;
    }

    @Column(name = "start_date", nullable = false)
    public Date getStartDate() {
        return startDate;
    }




}
