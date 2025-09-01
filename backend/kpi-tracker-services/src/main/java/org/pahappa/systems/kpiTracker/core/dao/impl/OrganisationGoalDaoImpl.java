package org.pahappa.systems.kpiTracker.core.dao.impl;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.dao.OrganisationGoalDao;
import org.pahappa.systems.kpiTracker.models.goalMgt.OrganisationGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.GoalPeriod;
import org.sers.webutils.model.RecordStatus;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class OrganisationGoalDaoImpl extends BaseDAOImpl<OrganisationGoal> implements OrganisationGoalDao {

    @Override
    public List<OrganisationGoal> findAllActive() {
        Search search = new Search();
        search.addFilterEqual("isActive", true);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.addSort("createdAt", false);
        return search(search);
    }

    @Override
    public List<OrganisationGoal> findByGoalPeriod(GoalPeriod goalPeriod) {
        Search search = new Search();
        search.addFilterEqual("goalPeriod", goalPeriod);
        search.addFilterEqual("isActive", true);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.addSort("createdAt", false);
        return search(search);
    }

    @Override
    public List<OrganisationGoal> findByOwner(String ownerId) {
        Search search = new Search();
        search.addFilterEqual("owner.id", ownerId);
        search.addFilterEqual("isActive", true);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.addSort("createdAt", false);
        return search(search);
    }

    @Override
    public List<OrganisationGoal> findByStatus(RecordStatus status) {
        Search search = new Search();
        search.addFilterEqual("recordStatus", status);
        search.addFilterEqual("isActive", true);
        search.addSort("createdAt", false);
        return search(search);
    }

    @Override
    public List<OrganisationGoal> findOverdueGoals() {
        Search search = new Search();
        search.addFilterLessThan("endDate", new Date());
        search.addFilterEqual("isActive", true);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.addFilterLessThan("progress", 100.0);
        search.addSort("endDate", true);
        return search(search);
    }

    @Override
    public List<OrganisationGoal> findByTitleContaining(String title) {
        Search search = new Search();
        search.addFilterLike("title", "%" + title + "%");
        search.addFilterEqual("isActive", true);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.addSort("createdAt", false);
        return search(search);
    }

    @Override
    public long countActiveGoals() {
        Search search = new Search();
        search.addFilterEqual("isActive", true);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        return count(search);
    }

    @Override
    public List<OrganisationGoal> findGoalsWithLowProgress(double threshold) {
        Search search = new Search();
        search.addFilterLessThan("progress", threshold);
        search.addFilterEqual("isActive", true);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.addSort("progress", true);
        return search(search);
    }
}
