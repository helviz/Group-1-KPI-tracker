package org.pahappa.systems.kpiTracker.core.dao.impl;

import org.pahappa.systems.kpiTracker.core.dao.StaffDao;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.springframework.stereotype.Repository;

@Repository("staffDao")
public class StaffDaoImpl extends BaseDAOImpl<Staff> implements StaffDao {
}