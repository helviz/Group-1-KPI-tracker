package org.pahappa.systems.kpiTracker.models.goalMgt;

public enum GoalLevel {
    ORGANISATION("Organisation Goal", 1),
    DEPARTMENT("Department Goal", 2),
    TEAM("Team Goal", 3),
    INDIVIDUAL("Individual Goal", 4);

    private final String displayName;
    private final int hierarchyLevel;

    GoalLevel(String displayName, int hierarchyLevel) {
        this.displayName = displayName;
        this.hierarchyLevel = hierarchyLevel;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getHierarchyLevel() {
        return hierarchyLevel;
    }

    public boolean isHigherThan(GoalLevel other) {
        return this.hierarchyLevel < other.hierarchyLevel;
    }

    public boolean isLowerThan(GoalLevel other) {
        return this.hierarchyLevel > other.hierarchyLevel;
    }

    public boolean canBeParentOf(GoalLevel childLevel) {
        return this.hierarchyLevel < childLevel.hierarchyLevel;
    }

    public static GoalLevel getParentLevel(GoalLevel childLevel) {
        switch (childLevel) {
            case DEPARTMENT:
                return ORGANISATION;
            case TEAM:
                return DEPARTMENT;
            case INDIVIDUAL:
                return TEAM;
            default:
                return null;
        }
    }

    public static GoalLevel getChildLevel(GoalLevel parentLevel) {
        switch (parentLevel) {
            case ORGANISATION:
                return DEPARTMENT;
            case DEPARTMENT:
                return TEAM;
            case TEAM:
                return INDIVIDUAL;
            default:
                return null;
        }
    }
}
