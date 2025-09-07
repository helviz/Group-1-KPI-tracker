package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.models.settings.PerformanceThresholds;

/**
 * Responsible for CRUD operations on {@link PerformanceThresholds}
 */
public interface PerformanceThresholdsService extends GenericService<PerformanceThresholds> {
    /**
     * Gets {@link PerformanceThresholds}
     *
     * @return
     */
    PerformanceThresholds getActivePerformanceThresholds();
}
