package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.Filter;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.pahappa.systems.kpiTracker.core.dao.AssignedUserDao;
import org.pahappa.systems.kpiTracker.core.dao.DepartmentDao;
import org.pahappa.systems.kpiTracker.core.dao.TeamDao;
import org.pahappa.systems.kpiTracker.core.services.AssignedUserService;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.models.team.Team;
import org.pahappa.systems.kpiTracker.models.user.AssignedUser;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("assignedUserService")
@Transactional
public class AssignedUserServiceImpl extends GenericServiceImpl<AssignedUser> implements AssignedUserService {

    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private TeamDao teamDao;

    @Autowired
    private AssignedUserDao assignedUserDao;

    @Autowired
    SessionFactory sessionFactory;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    // ---------------------------
    // Assignment Management
    // ---------------------------

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public AssignedUser assignUserToDepartment(User user, Department department) {
        if (user == null || department == null) {
            throw new IllegalArgumentException("User and Department cannot be null");
        }

        // Check if user is already assigned to this department
        AssignedUser existingAssignment = findAssignedUserByUser(user);

        if (existingAssignment != null) {
            // Update existing assignment
            existingAssignment.setDepartment(department);
            return assignedUserDao.save(existingAssignment);
        } else {
            // Create new assignment
            AssignedUser assignedUser = new AssignedUser();
            assignedUser.setUser(user);
            assignedUser.setDepartment(department);
            assignedUser.setRecordStatus(RecordStatus.ACTIVE);
            return assignedUserDao.save(assignedUser);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public AssignedUser assignUserToTeams(AssignedUser assignedUser, List<Team> teams) {
        if (assignedUser == null) {
            throw new IllegalArgumentException("AssignedUser cannot be null");
        }

        if (teams != null && !teams.isEmpty()) {
            // Clear existing teams and add new ones
            assignedUser.getAssignedTeams().clear();
            assignedUser.getAssignedTeams().addAll(teams);
        }

        return assignedUserDao.save(assignedUser);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public AssignedUser removeUserFromTeam(AssignedUser assignedUser, Team team) {
        if (assignedUser == null || team == null) {
            throw new IllegalArgumentException("AssignedUser and Team cannot be null");
        }

        assignedUser.getAssignedTeams().remove(team);
        return assignedUserDao.save(assignedUser);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public AssignedUser removeUserFromDepartment(AssignedUser assignedUser) {
        if (assignedUser == null) {
            throw new IllegalArgumentException("AssignedUser cannot be null");
        }

        // Set record status to deleted instead of actually removing
        assignedUser.setRecordStatus(RecordStatus.DELETED);
        return assignedUserDao.save(assignedUser);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public AssignedUser clearUserTeams(AssignedUser assignedUser) {
        if (assignedUser == null) {
            throw new IllegalArgumentException("AssignedUser cannot be null");
        }

        assignedUser.getAssignedTeams().clear();
        return assignedUserDao.save(assignedUser);
    }

    // ---------------------------
    // Retrieval Queries
    // ---------------------------

    @Override
    @Transactional(readOnly = true)
    public List<AssignedUser> getAssignedUsersByDepartment(Department department) {
        if (department == null) {
            return new ArrayList<>();
        }

        Search search = new Search();
        search.addFilterEqual("department", department);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.addSort("user.username", false, true);

        search.addFetch("user");

        return assignedUserDao.search(search);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignedUser> getAssignedUsersByTeam(Team team) {
        if (team == null) {
            return new ArrayList<>();
        }

        Search search = new Search();
        search.addFilterSome("teams", Filter.equal("id", team.getId()));
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.addSort("user.username", false, true);

        return assignedUserDao.search(search);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Team> getTeamsForUser(User user) {
        if (user == null) {
            return new ArrayList<>();
        }

        AssignedUser assignedUser = findAssignedUserByUser(user);
        if (assignedUser != null) {
            return new ArrayList<>(assignedUser.getAssignedTeams());
        }

        return new ArrayList<>();
    }

    @Override
    @Transactional(readOnly = true)
    public Department getDepartmentForUser(User user) {
        if (user == null) return null;

        AssignedUser assignedUser = findAssignedUserByUser(user);
        if (assignedUser != null && assignedUser.getDepartment() != null) {
            // Force initialization of lazy proxy
            assignedUser.getDepartment().getName();
            return assignedUser.getDepartment();
        }
        return null;
    }


    // ---------------------------
    // Batch Operations
    // ---------------------------

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void assignMultipleUsersToDepartment(List<User> users, Department department) {
        if (users == null || users.isEmpty() || department == null) {
            return;
        }

        for (User user : users) {
            try {
                assignUserToDepartment(user, department);
            } catch (Exception e) {
                String username = (user != null) ? user.getUsername() : "unknown";
                String departmentName = (department != null) ? department.getName() : "unknown";

                // Build the format string dynamically
                String format = "Failed to assign user " + username + " to department " + departmentName;

                // Log with the full exception stack trace
                log.error(format, e);
            }

        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void assignMultipleUsersToTeam(List<AssignedUser> assignedUsers, Team team) {
        if (assignedUsers == null || assignedUsers.isEmpty() || team == null) {
            return;
        }

        for (AssignedUser assignedUser : assignedUsers) {
            try {
                if (!assignedUser.getAssignedTeams().contains(team)) {
                    assignedUser.getAssignedTeams().add(team);
                    assignedUserDao.save(assignedUser);
                }
            } catch (Exception e) {
                String username = (assignedUser != null && assignedUser.getUser() != null)
                        ? assignedUser.getUser().getUsername()
                        : "unknown";
                String teamName = (team != null) ? team.getTeamName() : "unknown";

                // Build the format string with +
                String format = "Failed to assign user " + username + " to team " + teamName + ": {}";

                // Pass the exception message as the argument for {}
                log.error(format, e.getMessage());
            }

        }
    }

    // ---------------------------
    // Helper Methods
    // ---------------------------

    @Transactional(readOnly = true)
    public AssignedUser findAssignedUserByUser(User user) {
        if (user == null) {
            return null;
        }

        return assignedUserDao.searchUniqueByPropertyEqual("user", user, RecordStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public AssignedUser getAssignedUserById(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }

        return assignedUserDao.searchUniqueByPropertyEqual("id", id, RecordStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<AssignedUser> getAllAssignedUsers() {
        Search search = new Search();
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.addSort("user.username", false, true);

        return assignedUserDao.search(search);
    }

    @Transactional(readOnly = true)
    public List<AssignedUser> searchAssignedUsersByUsername(String username) {
        if (StringUtils.isBlank(username)) {
            return new ArrayList<>();
        }

        Search search = new Search();
        search.addFilterLike("user.username", "%" + username + "%");
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.addSort("user.username", false, true);

        return assignedUserDao.search(search);
    }

    @Transactional(readOnly = true)
    public int countAssignedUsersByDepartment(Department department) {
        if (department == null) {
            return 0;
        }

        Search search = new Search();
        search.addFilterEqual("department", department);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);

        return assignedUserDao.count(search);
    }

    @Transactional(readOnly = true)
    public int countAssignedUsersByTeam(Team team) {
        if (team == null) {
            return 0;
        }

        Search search = new Search();
        search.addFilterSome("teams", Filter.equal("id", team.getId()));
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);

        return assignedUserDao.count(search);
    }

    // ---------------------------
    // GenericService Implementation
    // ---------------------------

    @Override
    public boolean isDeletable(AssignedUser instance) throws OperationFailedException {
        if (instance == null) {
            return false;
        }

        // Add business logic to determine if the assigned user can be deleted
        // For example, check if the user has active KPIs or other dependencies

        return true; // Modify based on your business rules
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public AssignedUser saveInstance(AssignedUser entityInstance) throws ValidationFailedException, OperationFailedException {
        validateAssignedUser(entityInstance);
        return assignedUserDao.save(entityInstance);
    }

    // ---------------------------
    // Validation Methods
    // ---------------------------

    private void validateAssignedUser(AssignedUser assignedUser) throws ValidationFailedException {
        if (assignedUser == null) {
            throw new ValidationFailedException("AssignedUser cannot be null");
        }

        if (assignedUser.getUser() == null) {
            throw new ValidationFailedException("User is required for AssignedUser");
        }

        if (assignedUser.getDepartment() == null) {
            throw new ValidationFailedException("Department is required for AssignedUser");
        }

        // Check for duplicate assignments
        if (StringUtils.isBlank(assignedUser.getId())) {
            AssignedUser existing = findAssignedUserByUser(assignedUser.getUser());
            if (existing != null) {
                throw new ValidationFailedException("User " + assignedUser.getUser().getUsername() + " is already assigned to a department");
            }
        }
    }

    // ---------------------------
    // Pagination Support
    // ---------------------------

    @Transactional(readOnly = true)
    public List<AssignedUser> getAssignedUsers(int offset, int limit) {
        Search search = new Search();
        search.setFirstResult(offset);
        search.setMaxResults(limit);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.addSort("user.username", false, true);

        return assignedUserDao.search(search);
    }

    @Transactional(readOnly = true)
    public int getTotalAssignedUsersCount() {
        Search search = new Search();
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);

        return assignedUserDao.count(search);
    }

    @Transactional(readOnly = true)
    public List<AssignedUser> getAssignedUsersByDepartment(Department department, int offset, int limit) {
        if (department == null) {
            return new ArrayList<>();
        }

        Search search = new Search();
        search.addFilterEqual("department", department);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.setFirstResult(offset);
        search.setMaxResults(limit);
        search.addSort("user.username", false, true);

        return assignedUserDao.search(search);
    }
}