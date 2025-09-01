package org.pahappa.systems.kpiTracker.core.dao.impl;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.dao.TeamGoalDao;
import org.pahappa.systems.kpiTracker.models.goalMgt.TeamGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.DepartmentGoal;
import org.sers.webutils.model.RecordStatus;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class TeamGoalDaoImpl extends BaseDAOImpl<TeamGoal> implements TeamGoalDao {

    @Override
    public List<TeamGoal> findAllActive() {
        Search search = new Search();
        search.addFilterEqual("isActive", true);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.addSort("createdAt", false);
        return search(search);
    }

    @Override
    public List<TeamGoal> findByParentGoal(DepartmentGoal parentGoal) {
        Search search = new Search();
        search.addFilterEqual("parentGoal", parentGoal);
        search.addFilterEqual("isActive", true);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.addSort("createdAt", false);
        return search(search);
    }

    @Override
    public List<TeamGoal> findByTeamName(String teamName) {
        Search search = new Search();
        search.addFilterEqual("teamName", teamName);
        search.addFilterEqual("isActive", true);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.addSort("createdAt", false);
        return search(search);
    }

    @Override
    public List<TeamGoal> findByOwner(String ownerId) {
        Search search = new Search();
        search.addFilterEqual("owner.id", ownerId);
        search.addFilterEqual("isActive", true);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.addSort("createdAt", false);
        return search(search);
    }

    @Override
    public List<TeamGoal> findByStatus(RecordStatus status) {
        Search search = new Search();
        search.addFilterEqual("recordStatus", status);
        search.addFilterEqual("isActive", true);
        search.addSort("createdAt", false);
        return search(search);
    }

    @Override
    public List<TeamGoal> findOverdueGoals() {
        Search search = new Search();
        search.addFilterLessThan("endDate", new Date());
        search.addFilterEqual("isActive", true);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.addFilterLessThan("progress", 100.0);
        search.addSort("endDate", true);
        return search(search);
    }

    @Override
    public List<TeamGoal> findByTitleContaining(String title) {
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
    public List<TeamGoal> findGoalsWithLowProgress(double threshold) {
        Search search = new Search();
        search.addFilterLessThan("progress", threshold);
        search.addFilterEqual("isActive", true);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.addSort("progress", true);
        return search(search);
    }

    @Override
    public List<TeamGoal> findByParentGoalAndTeam(DepartmentGoal parentGoal, String teamName) {
        Search search = new Search();
        search.addFilterEqual("parentGoal", parentGoal);
        search.addFilterEqual("teamName", teamName);
        search.addFilterEqual("isActive", true);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.addSort("createdAt", false);
        return search(search);
    }
}
