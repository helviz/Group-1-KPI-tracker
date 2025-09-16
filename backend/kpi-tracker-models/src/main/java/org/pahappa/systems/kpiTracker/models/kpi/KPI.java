package org.pahappa.systems.kpiTracker.models.kpi;


import org.pahappa.systems.kpiTracker.constants.KpiType;
import org.pahappa.systems.kpiTracker.models.goalMgt.IndividualGoal;
import org.sers.webutils.model.BaseEntity;
import org.pahappa.systems.kpiTracker.models.staff.Staff;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Entity
@Table(name = "kpis")
public class KPI extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = true)
    private IndividualGoal goal;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "kpi_type", nullable = false)
    private KpiType kpiType;

    // Fields for Quantitative KPI
    @Column(name = "start_value", precision = 19, scale = 4)
    private BigDecimal startValue;

    @Column(name = "target_value", precision = 19, scale = 4)
    private BigDecimal targetValue;

    @Column(name = "current_value", precision = 19, scale = 4)
    private BigDecimal currentValue;

    @Column(name = "unit_of_measure")
    private String unitOfMeasure;

    // Field for QUALITATIVE KPI
    @Column(name = "is_complete")
    private Boolean isComplete = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_staff_id", nullable = false)
    private Staff ownerStaff;

    @Transient
    public BigDecimal getProgressPercentage() {
        if (this.kpiType == null) {
            return BigDecimal.ZERO;
        }

        switch (this.kpiType) {
            case QUANTITATIVE:
                if (startValue == null || targetValue == null || currentValue == null || targetValue.subtract(startValue).compareTo(BigDecimal.ZERO) == 0) {
                  
                 return BigDecimal.ZERO;
                }
                // Progress % = ((Current - Start) / (Target - Start)) * 100
                BigDecimal progress = currentValue.subtract(startValue)
                        .divide(targetValue.subtract(startValue), 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));

                // Clamp progress between 0 and 100
                if (progress.compareTo(new BigDecimal("100")) > 0) {
                    progress = new BigDecimal("100");
                }
                if (progress.compareTo(BigDecimal.ZERO) < 0) {
                    progress = BigDecimal.ZERO;
                }
                return progress.setScale(2, RoundingMode.HALF_UP);

            case QUALITATIVE:
                return (isComplete != null && isComplete) ? new BigDecimal("100") : BigDecimal.ZERO;

            default:
                return BigDecimal.ZERO;
        }
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public KpiType getKpiType() {
        return kpiType;
    }

    public void setKpiType(KpiType kpiType) {
        this.kpiType = kpiType;
    }

    public BigDecimal getStartValue() {
        return startValue;
    }

    public void setStartValue(BigDecimal startValue) {
        this.startValue = startValue;
    }

    public BigDecimal getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(BigDecimal targetValue) {
        this.targetValue = targetValue;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue = currentValue;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public Staff getOwnerStaff() {
        return ownerStaff;
    }

    public void setOwnerStaff(Staff ownerStaff) {
        this.ownerStaff = ownerStaff;
    }

    public Boolean getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(Boolean isComplete) {
        this.isComplete = isComplete;
    }


    public IndividualGoal getGoal() {
        return goal;
    }

    public void setGoal(IndividualGoal goal) {
        this.goal = goal;
    }

    @Override
    public String toString() {
        return this.title;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        KPI kpi = (KPI) o;
        return super.getId() != null && Objects.equals(super.getId(), kpi.getId());
    }

}