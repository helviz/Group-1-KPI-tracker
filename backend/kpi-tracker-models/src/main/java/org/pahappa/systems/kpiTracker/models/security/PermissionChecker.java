package org.pahappa.systems.kpiTracker.models.security;


import org.sers.webutils.model.security.Permission;
import org.sers.webutils.model.security.Role;
import org.sers.webutils.model.security.User;

import java.util.Set;

public class PermissionChecker {

    public static boolean hasPermission(User user, String permissionName) {
        if (user == null || user.getRoles() == null) {
            return false;
        }

        for (Role role : user.getRoles()) {
            Set<Permission> permissions = role.getPermissions();
            if (permissions != null) {
                for (Permission perm : permissions) {
                    if (perm != null && perm.getName().equalsIgnoreCase(permissionName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void checkPermission(User user, String permissionName) {
        if (!hasPermission(user, permissionName)) {
            System.out.println(" ALERT: User " + (user != null ? user.getUsername() : "Anonymous") +
                    " attempted access without permission: " + permissionName);
            throw new SecurityException("Access denied: Missing permission " + permissionName);
        }
    }
}
