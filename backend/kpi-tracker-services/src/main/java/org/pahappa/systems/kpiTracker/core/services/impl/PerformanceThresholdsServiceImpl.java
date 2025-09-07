package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.services.PerformanceThresholdsService;
import org.pahappa.systems.kpiTracker.models.settings.PerformanceThresholds;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PerformanceThresholdsServiceImpl extends GenericServiceImpl<PerformanceThresholds>
        implements PerformanceThresholdsService {
    @Override
    public PerformanceThresholds saveInstance(PerformanceThresholds entityInstance)
            throws ValidationFailedException, OperationFailedException {
        Validate.notNull(entityInstance, "Missing performance thresholds details");
        Validate.notNull(entityInstance.getRewardEligibilityThreshold(), "Missing reward eligibility threshold");
        Validate.notNull(entityInstance.getPipInitiationThreshold(), "Missing PIP initiation threshold");

        // Validate threshold ranges
        if (entityInstance.getRewardEligibilityThreshold() < 0
                || entityInstance.getRewardEligibilityThreshold() > 100) {
            throw new ValidationFailedException("Reward eligibility threshold must be between 0 and 100");
        }

        if (entityInstance.getPipInitiationThreshold() < 0 || entityInstance.getPipInitiationThreshold() > 100) {
            throw new ValidationFailedException("PIP initiation threshold must be between 0 and 100");
        }

        if (entityInstance.getRewardEligibilityThreshold() <= entityInstance.getPipInitiationThreshold()) {
            throw new ValidationFailedException(
                    "Reward eligibility threshold must be greater than PIP initiation threshold");
        }

        PerformanceThresholds existingThresholds = getActivePerformanceThresholds();
        if (existingThresholds != null && !existingThresholds.equals(entityInstance))
            throw new ValidationFailedException(
                    "Performance thresholds already exist in the database. Retrieve a copy and make appropriate edits.");

        return super.save(entityInstance);
    }

    @Override
    public boolean isDeletable(PerformanceThresholds instance) throws OperationFailedException {
        return false;
    }

    @Override
    public PerformanceThresholds getActivePerformanceThresholds() {
        return super.searchUnique(new Search().addFilterEqual("recordStatus", RecordStatus.ACTIVE));
    }
}