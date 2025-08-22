package org.pahappa.systems.kpiTracker.models.security;

public final class PermissionConstants {
    private PermissionConstants() {
    }

    @SystemPermission(name = "Api user", description = "Has role for api users")
    public static final String PERM_API_USER = "Api User";

    @SystemPermission(name = "View Teams", description = "Allows viewing the teams page")
    public static final String PERM_VIEW_TEAMS = "PERM_VIEW_TEAMS";

    // KPI Permissions
    @SystemPermission(name = "Create KPIs", description = "Allows creating KPIs for goals")
    public static final String PERM_CREATE_KPI = "PERM_CREATE_KPI";

    @SystemPermission(name = "View KPIs", description = "Allows viewing KPIs")
    public static final String PERM_VIEW_KPI = "PERM_VIEW_KPI";

    @SystemPermission(name = "Update KPIs", description = "Allows updating KPIs")
    public static final String PERM_UPDATE_KPI = "PERM_UPDATE_KPI";

    @SystemPermission(name = "Delete KPIs", description = "Allows deleting KPIs")
    public static final String PERM_DELETE_KPI = "PERM_DELETE_KPI";
}
