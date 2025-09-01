package org.pahappa.systems.kpiTracker.constants;

import lombok.Getter;

@Getter
public enum ActivityStatus {
    NOT_STARTED("Not Started"),
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String displayName;

    ActivityStatus(String displayName) {
        this.displayName = displayName;
    }

}
