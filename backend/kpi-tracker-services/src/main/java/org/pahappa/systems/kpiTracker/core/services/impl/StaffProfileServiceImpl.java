package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.dao.StaffProfileDao;
import org.pahappa.systems.kpiTracker.core.services.StaffProfileService;
import org.pahappa.systems.kpiTracker.models.user.StaffProfile;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Level;
import java.util.logging.Logger; // Use java.util.logging.Logger

@Service("staffProfileService")
@Transactional
public class StaffProfileServiceImpl extends GenericServiceImpl<StaffProfile> implements StaffProfileService {

    private static final Logger LOGGER = Logger.getLogger(StaffProfileServiceImpl.class.getName());

    @Autowired
    private StaffProfileDao staffProfileDao;

    @Override
    public StaffProfile saveInstance(StaffProfile entityInstance) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(entityInstance, "Staff profile details cannot be null");
        Validate.notNull(entityInstance.getUser(), "Staff profile must be linked to a user");
        Validate.notNull(entityInstance.getDepartment(), "Staff profile must be linked to a department");


        // Check for duplicate user assignment (should be unique per user)
        StaffProfile existingProfileForUser = staffProfileDao.findByUser(entityInstance.getUser());
        if (existingProfileForUser != null && !existingProfileForUser.equals(entityInstance)) {
            throw new ValidationFailedException("A staff profile already exists for user '" + entityInstance.getUser().getUsername() + "'");
        }


        return super.save(entityInstance);
    }

    @Override
    public boolean isDeletable(StaffProfile instance) throws OperationFailedException {
        
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public StaffProfile findStaffProfileByUser(User user) throws ValidationFailedException {
        Validate.notNull(user, "User cannot be null when searching for staff profile");
        return staffProfileDao.findByUser(user);
    }
}