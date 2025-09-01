package org.pahappa.systems.kpiTracker.core.dao.impl;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.dao.IndividualGoalDao;
import org.pahappa.systems.kpiTracker.models.goalMgt.IndividualGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.TeamGoal;
import org.sers.webutils.model.RecordStatus;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class IndividualGoalDaoImpl extends BaseDAOImpl<IndividualGoal> implements IndividualGoalDao {

    @Override
    public List<IndividualGoal> findAllActive() {
        Search search = new Search();
        search.addFilterEqual("isActive", true);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.addSort("createdAt", false);
        return search(search);
    }

    @Override
    public List<IndividualGoal> findByParentGoal(TeamGoal parentGoal) {
        Search search = new Search();
        search.addFilterEqual("parentGoal", parentGoal);
        search.addFilterEqual("isActive", true);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.addSort("createdAt", false);
        return search(search);
    }

    @Override
    public List<IndividualGoal> findByOwner(String ownerId) {
        Search search = new Search();
        search.addFilterEqual("owner.id", ownerId);
        search.addFilterEqual("isActive", true);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.addSort("createdAt", false);
        return search(search);
    }

    @Override
    public List<IndividualGoal> findByOwnerName(String ownerName) {
        Search search = new Search();
        search.addFilterLike("ownerName", "%" + ownerName + "%");
        search.addFilterEqual("isActive", true);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.addSort("createdAt", false);
        return search(search);
    }

    @Override
    public List<IndividualGoal> findByDepartment(String department) {
        Search search = new Search();
        search.addFilterEqual("department", department);
        search.addFilterEqual("isActive", true);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.addSort("createdAt", false);
        return search(search);
    }

    @Override
    public List<IndividualGoal> findByTeam(String team) {
        Search search = new Search();
        search.addFilterEqual("team", team);
        search.addFilterEqual("isActive", true);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.addSort("createdAt", false);
        return search(search);
    }

    @Override
    public List<IndividualGoal> findByStatus(RecordStatus status) {
        Search search = new Search();
        search.addFilterEqual("recordStatus", status);
        search.addFilterEqual("isActive", true);
        search.addSort("createdAt", false);
        return search(search);
    }

    @Override
    public List<IndividualGoal> findOverdueGoals() {
        Search search = new Search();
        search.addFilterLessThan("endDate", new Date());
        search.addFilterEqual("isActive", true);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.addFilterLessThan("progress", 100.0);
        search.addSort("endDate", true);
        return search(search);
    }

    @Override
    public List<IndividualGoal> findByTitleContaining(String title) {
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
    public List<IndividualGoal> findGoalsWithLowProgress(double threshold) {
        Search search = new Search();
        search.addFilterLessThan("progress", threshold);
        search.addFilterEqual("isActive", true);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.addSort("progress", true);
        return search(search);
    }

    @Override
    public List<IndividualGoal> findByParentGoalAndOwner(TeamGoal parentGoal, String ownerId) {
        Search search = new Search();
        search.addFilterEqual("parentGoal", parentGoal);
        search.addFilterEqual("owner.id", ownerId);
        search.addFilterEqual("isActive", true);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.addSort("createdAt", false);
        return search(search);
    }
}
