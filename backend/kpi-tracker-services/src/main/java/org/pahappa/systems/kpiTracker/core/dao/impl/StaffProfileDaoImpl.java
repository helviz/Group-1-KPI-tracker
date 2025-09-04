package org.pahappa.systems.kpiTracker.core.dao.impl;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.dao.StaffProfileDao;
import org.pahappa.systems.kpiTracker.models.user.StaffProfile;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.security.User;
import org.springframework.stereotype.Repository;

@Repository
public class StaffProfileDaoImpl extends BaseDAOImpl<StaffProfile> implements StaffProfileDao {

    @Override
    public StaffProfile findByUser(User user) {
        Search search = new Search();
        search.addFilterEqual("user", user);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        return searchUnique(search);
    }
}