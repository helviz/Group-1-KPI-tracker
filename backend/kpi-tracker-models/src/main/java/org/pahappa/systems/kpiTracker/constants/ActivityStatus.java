package org.pahappa.systems.kpiTracker.constants;

public enum ActivityStatus {
    DONE("Done"),
    ON_GOING("On Going"),
    NOT_STARTED("Not Started");

    private final String displayName;

    ActivityStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}