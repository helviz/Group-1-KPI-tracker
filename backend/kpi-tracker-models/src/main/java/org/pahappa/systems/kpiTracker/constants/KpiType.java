package org.pahappa.systems.kpiTracker.constants;

public enum KpiType {
    QUANTITATIVE("Quantitative (Metric-Based)"),
    QUALITATIVE("Qualitative (Milestone-Based)");

    private final String displayName;

    KpiType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}