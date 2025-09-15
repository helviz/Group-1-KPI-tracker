package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.search.Search;
import org.apache.commons.lang.StringUtils;
import org.pahappa.systems.kpiTracker.core.dao.DepartmentDao;
import org.pahappa.systems.kpiTracker.core.dao.StaffDao;
import org.pahappa.systems.kpiTracker.core.dao.StaffTeamDao;
import org.pahappa.systems.kpiTracker.core.dao.TeamDao;
import org.pahappa.systems.kpiTracker.core.services.StaffService;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.models.staff.StaffTeam;
import org.pahappa.systems.kpiTracker.models.team.Team;
import org.pahappa.systems.kpiTracker.models.security.RoleConstants;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.User;
import org.sers.webutils.model.security.Role;
import org.sers.webutils.server.core.dao.RoleDao;
import org.sers.webutils.server.core.service.RoleService;
import org.sers.webutils.server.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.LinkedHashSet;

@Service("staffService")
@Transactional
public class StaffServiceImpl extends GenericServiceImpl<Staff> implements StaffService {

    private static final Logger log = LoggerFactory.getLogger(StaffServiceImpl.class);

    private final StaffDao staffDao;
    private final StaffTeamDao staffTeamDao;
    private final DepartmentDao departmentDao;
    private final TeamDao teamDao;
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    RoleDao roleDao;

    @Autowired
    public StaffServiceImpl(StaffDao staffDao, StaffTeamDao staffTeamDao, DepartmentDao departmentDao, TeamDao teamDao,
                            UserService userService, RoleService roleService) {
        this.staffDao = staffDao;
        this.staffTeamDao = staffTeamDao;
        this.departmentDao = departmentDao;
        this.teamDao = teamDao;
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Staff createOrUpdateStaff(User user, Department department) throws ValidationFailedException {
        if (user == null || department == null) {
            throw new ValidationFailedException("User and Department cannot be null");
        }

        Staff staff = getStaffByUser(user);
        if (staff != null) {
            staff.setDepartment(department);
            staff.setActive(true);
            staff.setRecordStatus(RecordStatus.ACTIVE);
            return staffDao.save(staff);
        } else {
            staff = new Staff();
            staff.setUser(user);
            staff.setDepartment(department);
            return saveStaff(staff);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void assignStaffToTeams(Staff staff, List<Team> teams) throws ValidationFailedException {
        if (staff == null) {
            throw new ValidationFailedException("Staff cannot be null");
        }
        clearStaffTeams(staff);
        if (teams != null) {
            for (Team team : teams) {
                assignStaffToTeam(staff, team);
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void assignStaffToTeam(Staff staff, Team team) throws ValidationFailedException {
        if (staff == null || team == null) {
            throw new ValidationFailedException("Staff and Team cannot be null");
        }
        if (!isStaffInTeam(staff, team)) {
            StaffTeam staffTeam = new StaffTeam();
            staffTeam.setStaff(staff);
            staffTeam.setTeam(team);
            staffTeamDao.save(staffTeam);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeStaffFromTeam(Staff staff, Team team) throws ValidationFailedException {
        if (staff == null || team == null) {
            throw new ValidationFailedException("Staff and Team cannot be null");
        }
        Search search = new Search(StaffTeam.class);
        search.addFilterEqual("staff", staff);
        search.addFilterEqual("team", team);
        StaffTeam assignment = staffTeamDao.searchUnique(search);
        if (assignment != null) {
            staffTeamDao.remove(assignment);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deactivateStaff(Staff staff) throws ValidationFailedException {
        if (staff == null) {
            throw new ValidationFailedException("Staff cannot be null");
        }
        staff.setActive(false);
        staffDao.save(staff);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void activateStaff(Staff staff) {
        if (staff == null) return;
        staff.setActive(true);
        staffDao.save(staff);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void clearStaffTeams(Staff staff) {
        if (staff == null) return;
        Search search = new Search(StaffTeam.class);
        search.addFilterEqual("staff", staff);
        List<StaffTeam> assignments = staffTeamDao.search(search);
        for (StaffTeam assignment : assignments) {
            staffTeamDao.remove(assignment);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Staff> getStaffByDepartment(Department department) {
        if (department == null) return new ArrayList<>();
        Search search = new Search();
        search.addFilterEqual("department", department);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.addFilterEqual("active", true);
        search.addSort("user.firstName", false, true);
        search.addFetch("user");
        return staffDao.search(search);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Staff> getStaffByTeam(Team team) {
        if (team == null) return new ArrayList<Staff>();
        Search search = new Search(StaffTeam.class);
        search.addFilterEqual("team", team);
        // Filter on the joined staff entity directly in the query for efficiency
        search.addFilterEqual("staff.active", true);
        search.addFilterEqual("staff.recordStatus", RecordStatus.ACTIVE);
        search.addFetch("staff");
        search.addFetch("staff.user");
        search.addSort("staff.user.firstName", false, true);

        List<StaffTeam> assignments = staffTeamDao.search(search);

        // Use a LinkedHashSet to preserve the sort order from the query while ensuring uniqueness
        Set<Staff> uniqueStaff = new LinkedHashSet<Staff>();
        for (StaffTeam assignment : assignments) {
            if (assignment.getStaff() != null) {
                uniqueStaff.add(assignment.getStaff());
            }
        }
        return new ArrayList<Staff>(uniqueStaff);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Team> getTeamsOfStaff(Staff staff) {
        if (staff == null) return new ArrayList<Team>();
        Search search = new Search(StaffTeam.class);
        search.addFilterEqual("staff", staff);
        search.addFetch("team");
        List<StaffTeam> assignments = staffTeamDao.search(search);
        List<Team> teams = new ArrayList<Team>();
        for (StaffTeam assignment : assignments) {
            teams.add(assignment.getTeam());
        }
        return teams;
    }

    @Override
    @Transactional(readOnly = true)
    public Department getDepartmentForUser(User user) {
        if (user == null) return null;
        Staff staff = getStaffByUser(user);
        return (staff != null) ? staff.getDepartment() : null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void clearTeamMembers(Team team) {
        if (team == null) return;
        Search search = new Search(StaffTeam.class);
        search.addFilterEqual("team", team);
        List<StaffTeam> assignments = staffTeamDao.search(search);
        for (StaffTeam assignment : assignments) {
            staffTeamDao.remove(assignment);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createMultipleStaff(List<User> users, Department department) {
        if (users == null || users.isEmpty() || department == null) return;
        for (User user : users) {
            try {
                createOrUpdateStaff(user, department);
            } catch (Exception e) {
                log.error(String.format("Failed to create staff for user %s in department %s", user.getUsername(), department.getName()), e);
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void assignMultipleStaffToTeam(List<Staff> staffList, Team team) {
        if (staffList == null || staffList.isEmpty() || team == null) return;
        for (Staff staff : staffList) {
            try {
                assignStaffToTeam(staff, team);
            } catch (Exception e) {
                log.error(String.format("Failed to assign staff %s to team %s", staff.getUser().getUsername(), team.getTeamName()), e);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Staff getStaffByUser(User user) {
        if (user == null) return null;
        return staffDao.searchUniqueByPropertyEqual("user", user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Staff> searchStaffByUsername(String username) {
        if (StringUtils.isBlank(username)) return new ArrayList<>();
        Search search = new Search();
        search.addFilterLike("user.username", "%" + username + "%");
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        search.addFilterEqual("active", true);
        search.addSort("user.username", false, true);
        return staffDao.search(search);
    }

    @Override
    public boolean isDeletable(Staff instance) {
        return false; // Prefer deactivation over deletion
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Staff saveStaff(Staff staff) throws ValidationFailedException {
        Validate.notNull(staff, "Staff details cannot be null");
        Validate.hasText(staff.getFirstName(), "First name is required");
        Validate.hasText(staff.getLastName(), "Last name is required");
        Validate.hasText(staff.getEmail(), "Email is required");
        Validate.notNull(staff.getDepartment(), "Staff must have a Department");

        if (StringUtils.isBlank(staff.getId())) {
            Search search = new Search(Staff.class);
            search.addFilterEqual("email", staff.getEmail());
            if (staffDao.count(search) > 0) {
                throw new ValidationFailedException("A staff member with the email " + staff.getEmail() + " already exists.");
            }
        }
        return staffDao.merge(staff);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUnassignedUsersForTeam(Team team) {
        if (team == null || team.getDepartment() == null) return new ArrayList<User>();

        List<Staff> staffInDepartment = getStaffByDepartment(team.getDepartment());
        List<Staff> staffInTeam = getStaffByTeam(team);

        Set<String> staffIdsInTeam = new HashSet<String>();
        for (Staff staff : staffInTeam) {
            staffIdsInTeam.add(staff.getId());
        }

        List<User> unassignedUsers = new ArrayList<User>();
        for (Staff staff : staffInDepartment) {
            if (!staffIdsInTeam.contains(staff.getId())) {
                unassignedUsers.add(staff.getUser());
            }
        }
        return unassignedUsers;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Staff> getUnassignedStaffInDepartment(Department department) {
        if (department == null) {
            return new ArrayList<Staff>();
        }

        // 1. Get all staff in the department
        List<Staff> staffInDepartment = getStaffByDepartment(department);

        // 2. Get all teams in the department
        Search teamSearch = new Search(Team.class);
        teamSearch.addFilterEqual("department", department);
        List<Team> teamsInDepartment = teamDao.search(teamSearch);

        // 3. Collect all staff IDs of members who are already in any team in this department
        Set<String> staffIdsInAnyTeam = new HashSet<String>();
        for (Team team : teamsInDepartment) {
            List<Staff> staffInTeam = getStaffByTeam(team);
            for (Staff staff : staffInTeam) {
                staffIdsInAnyTeam.add(staff.getId());
            }
        }

        // 4. Filter the department's staff to find those not in any team
        List<Staff> unassignedStaff = new ArrayList<Staff>();
        for (Staff staff : staffInDepartment) {
            if (!staffIdsInAnyTeam.contains(staff.getId())) {
                unassignedStaff.add(staff);
            }
        }
        return unassignedStaff;
    }


    private boolean isStaffInTeam(Staff staff, Team team) {
        Search search = new Search(StaffTeam.class);
        search.addFilterEqual("staff", staff);
        search.addFilterEqual("team", team);
        return staffTeamDao.count(search) > 0;
    }

    @Override
    public Staff saveInstance(Staff entityInstance) throws ValidationFailedException, OperationFailedException {
        return saveStaff(entityInstance);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Staff createUser(Staff staff) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(staff, "Staff details cannot be null.");
        Validate.isTrue(staff.getUser() == null, "This staff member already has a user account.");
        Validate.hasText(staff.getEmail(), "Staff email is required to create a user account.");
        Validate.hasText(staff.getId(), "Staff must be a saved entity to create a user account for it.");

        if (userService.getUserByUsername(staff.getEmail()) != null) {
            throw new ValidationFailedException("A user with the email/username '" + staff.getEmail() + "' already exists.");
        }

        User user = new User();
        user.setUsername(staff.getEmail());
        user.setClearTextPassword("Welcome@123");
        user.setChangePassword(true);
        user.setFirstName(staff.getFirstName());
        user.setLastName(staff.getLastName());
        user.setEmailAddress(staff.getEmail());

        // Assign a default role
        Role staffRole = roleService.getRoleByName(RoleConstants.ROLE_STAFF);
        if (staffRole == null) {
            throw new OperationFailedException("Default 'Staff' role not found. Please ensure migrations are run or the role is created manually.");
        }
        user.setRoles(new HashSet<>(Collections.singletonList(staffRole)));

        User savedUser = userService.saveUser(user);

        // The 'staff' object passed to this method is a "detached" entity from the web layer.
        // Merging it directly can cause issues if it has a complex graph of other detached objects
        // (like Department -> departmentLead -> User), leading to the "Unable to find User" error.
        //
        // The correct approach is to load the "managed" instance of the Staff entity from the database
        // within the current transaction, update it, and then save it.
        Staff managedStaff = staffDao.find(staff.getId());
        if (managedStaff == null) {
            throw new OperationFailedException("Could not find the staff member to associate the user with. The staff member may have been deleted.");
        }
        managedStaff.setUser(savedUser);
        return this.saveStaff(managedStaff);
    }
}