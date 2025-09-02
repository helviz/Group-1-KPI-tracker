package org.pahappa.systems.kpiTracker.constants;

import lombok.Getter;

@Getter
public enum ActivityPriority {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    CRITICAL("Critical");

    private final String displayName;

    ActivityPriority(String displayName) {
        this.displayName = displayName;
    }

}