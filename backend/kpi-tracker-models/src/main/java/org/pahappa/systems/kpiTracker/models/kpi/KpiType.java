package org.pahappa.systems.kpiTracker.models.kpi;

public enum KpiType {
    NUMERICAL("Numerical (Metric-Based)"),
    BINARY("Binary (Milestone-Based)");

    private final String displayName;

    KpiType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}