package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.models.user.StaffProfile;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.User;

public interface StaffProfileService extends GenericService<StaffProfile> {
    /**
     * Finds the StaffProfile associated with a given User.
     * @param user The User entity.
     * @return The StaffProfile.
     * @throws ValidationFailedException If the user is null.
     */
    StaffProfile findStaffProfileByUser(User user) throws ValidationFailedException;
}