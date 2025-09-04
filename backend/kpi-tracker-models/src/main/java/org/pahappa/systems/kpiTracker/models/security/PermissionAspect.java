package org.pahappa.systems.kpiTracker.models.security;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.sers.webutils.model.security.User;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PermissionAspect {

    private final User loggedInUser;

    public PermissionAspect(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    @Before("@annotation(requiresPermission)")
    public void checkPermission(RequiresPermission requiresPermission) {
        String required = requiresPermission.value();
        PermissionChecker.checkPermission(loggedInUser, required);
    }
}
