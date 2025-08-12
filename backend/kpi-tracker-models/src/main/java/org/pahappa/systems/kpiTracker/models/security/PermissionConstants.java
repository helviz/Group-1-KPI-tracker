package org.pahappa.systems.kpiTracker.models.security;

public final class PermissionConstants {
    private PermissionConstants() {
    }

    @SystemPermission(name = "Api user", description = "Has role for api users")
    public static final String PERM_API_USER = "Api User";

    @SystemPermission(name = "View Teams", description = "Allows viewing the teams page")
    public static final String PERM_VIEW_TEAMS = "PERM_VIEW_TEAMS";

}
