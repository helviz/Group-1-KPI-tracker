package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.services.KpiService;
import org.pahappa.systems.kpiTracker.models.goal.Goal;
import org.pahappa.systems.kpiTracker.models.kpi.KPI;
import org.pahappa.systems.kpiTracker.models.kpi.KpiType;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("kpiService")
@Transactional
public class KpiServiceImpl extends GenericServiceImpl<KPI> implements KpiService {

    @Override
    public KPI saveInstance(KPI kpi) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(kpi, "KPI details cannot be null");
        Validate.notNull(kpi.getGoal(), "KPI must be attached to a goal");
        Validate.hasText(kpi.getTitle(), "KPI title is required");
        Validate.notNull(kpi.getKpiType(), "KPI type is required");

        if (isDuplicate(kpi, "title", kpi.getTitle())) {
            throw new ValidationFailedException("A KPI with the same title already exists for this goal.");
        }

        if (kpi.getKpiType() == KpiType.NUMERICAL) {
            Validate.notNull(kpi.getStartValue(), "Start value is required for a numerical KPI");
            Validate.notNull(kpi.getTargetValue(), "Target value is required for a numerical KPI");
            Validate.isTrue(kpi.getTargetValue().compareTo(kpi.getStartValue()) > 0, "Target value must be greater than the start value.");

            // Initialize current value if not set
            if (kpi.isNew() && kpi.getCurrentValue() == null) {
                kpi.setCurrentValue(kpi.getStartValue());
            }

            // Clear binary fields to ensure data integrity
            kpi.setIsComplete(null);

        } else if (kpi.getKpiType() == KpiType.BINARY) {
            // Initialize binary fields if not set
            if (kpi.isNew() && kpi.getIsComplete() == null) {
                kpi.setIsComplete(false);
            }

            // Clear numerical fields to ensure data integrity
            kpi.setStartValue(null);
            kpi.setTargetValue(null);
            kpi.setCurrentValue(null);
            kpi.setUnitOfMeasure(null);
        }

        return super.save(kpi);
    }

    @Override
    public boolean isDeletable(KPI instance) throws OperationFailedException {
        // For now, we allow deletion.
        // Future logic could prevent deletion if it's part of a performance review.
        return true;
    }

    @Override
    public List<KPI> getKpisForGoal(Goal goal) throws ValidationFailedException {
        Validate.notNull(goal, "Goal cannot be null");
        Search search = new Search();
        search.addFilterEqual("goal", goal);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        return super.search(search);
    }

    private boolean isDuplicate(KPI entity, String property, Object value) {
        Search search = new Search().addFilterEqual(property, value).addFilterEqual("goal", entity.getGoal());
        if (!entity.isNew()) search.addFilterNotEqual("id", entity.getId());
        return super.count(search) > 0;
    }
}