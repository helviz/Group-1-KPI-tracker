package org.pahappa.systems.kpiTracker.core.dao.impl;

import org.pahappa.systems.kpiTracker.core.dao.KpiDao;
import org.pahappa.systems.kpiTracker.models.kpi.KPI;
import org.springframework.stereotype.Repository;

@Repository("kpiDao")
public class KpiDaoImpl extends BaseDAOImpl<KPI> implements KpiDao {
}