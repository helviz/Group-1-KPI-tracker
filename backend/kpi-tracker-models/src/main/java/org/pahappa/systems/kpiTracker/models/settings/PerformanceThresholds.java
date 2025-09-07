package org.pahappa.systems.kpiTracker.models.settings;

import lombok.Setter;
import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;


@Setter
@Entity
@Table(name = "performance_thresholds")
@Inheritance(strategy = InheritanceType.JOINED)
public class PerformanceThresholds extends BaseEntity {


    private double rewardEligibilityThreshold = 95.0;

    private double  pipInitiationThreshold = 70.0;


    @Column(name = "reward_eligibility_threshold", nullable = false)
    public double getRewardEligibilityThreshold() {
        return rewardEligibilityThreshold;
    }


    @Column(name = "pip_initiation_threshold", nullable = false)
    public double getPipInitiationThreshold() {
        return pipInitiationThreshold;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof PerformanceThresholds && (super.getId() != null)
                ? super.getId().equals(((PerformanceThresholds) object).getId())
                : (object == this);
    }

    @Override
    public int hashCode() {
        return super.getId() != null ? this.getClass().hashCode() + super.getId().hashCode() : super.hashCode();
    }
}
