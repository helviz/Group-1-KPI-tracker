package org.pahappa.systems.kpiTracker.models.security;

public final class RoleConstants {
	private RoleConstants() {

	}
	
	@SystemRole(name = "Api user", description = "Has role for api users")
	public static final String ROLE_API_USER = "Api User";

	@SystemRole(name = "Staff", description = "Default role for staff members with a user account.")
	public static final String ROLE_STAFF = "Staff";


}
