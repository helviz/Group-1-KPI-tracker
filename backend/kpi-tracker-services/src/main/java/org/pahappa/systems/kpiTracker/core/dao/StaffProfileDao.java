package org.pahappa.systems.kpiTracker.core.dao;

import org.pahappa.systems.kpiTracker.models.user.StaffProfile;
import org.sers.webutils.model.security.User;

public interface StaffProfileDao extends BaseDao<StaffProfile>{
    /**
     * Finds a StaffProfile associated with a specific User.
     * @param user The User entity.
     * @return The StaffProfile or null if not found.
     */
    StaffProfile findByUser(User user);
}

