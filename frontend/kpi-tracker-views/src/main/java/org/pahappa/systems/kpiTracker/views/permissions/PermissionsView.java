
package org.pahappa.systems.kpiTracker.views.permissions;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.models.security.PermissionConstants;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.sers.webutils.model.security.Permission;
import org.sers.webutils.model.security.Role;
import org.sers.webutils.server.core.service.PermissionService;
import org.sers.webutils.server.core.service.RoleService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@ManagedBean(name = "permissionView")
@SessionScoped
public class PermissionsView implements Serializable {

    private static final Map<String, String> PERMISSION_CATEGORY_MAP = new HashMap<>();

    static {
        // Initialize the map to group permission names by category
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_API_USER, "General");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_WEB_ACCESS, "General");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_CREATE_USERS, "User Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_READ_VIEW_USERS, "User Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_UPDATE_USER, "User Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_DEACTIVATE_REACTIVATE_USER, "User Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_BULK_IMPORT_USERS, "User Management");

        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_CREATE_ROLE, "Role Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_READ_VIEW_ROLES, "Role Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_UPDATE_ROLE, "Role Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_DELETE_ROLE, "Role Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_ASSIGN_ROLES_TO_USER, "Role Management");


        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_CREATE_DEPARTMENT, "Department Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_VIEW_DEPARTMENTS, "Department Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_UPDATE_DEPARTMENT, "Department Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_DELETE_DEPARTMENT, "Department Management");

        // ====================== Team Management Permissions ======================
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_CREATE_TEAM, "Team Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_READ_VIEW_TEAM, "Team Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_UPDATE_TEAM, "Team Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_DEACTIVATE_TEAM, "Team Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_DELETE_TEAM, "Team Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_VIEW_TEAMS_PAGE, "Team Management");

        // ====================== Goal Management Permissions ======================
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_ADD_GOAL, "Goal Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_VIEW_GOAL, "Goal Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_UPDATE_GOAL, "Goal Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_DELETE_GOAL, "Goal Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_APPROVE_GOAL, "Goal Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_REQUEST_CHANGES_FOR_GOAL, "Goal Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_REJECT_GOAL, "Goal Management");

        // ====================== Key Performance Indicator (KPI) Management Permissions ======================
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_CREATE_KPIS, "Key Performance Indicator (KPI) Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_VIEW_KPIS, "Key Performance Indicator (KPI) Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_UPDATE_KPIS, "Key Performance Indicator (KPI) Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_DELETE_KPIS, "Key Performance Indicator (KPI) Management");

        // ====================== Goal Activity Management Permissions ======================
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_ATTACH_NEW_ACTIVITIES, "Goal Activity Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_EDIT_EXISTING_ACTIVITIES, "Goal Activity Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_DELETE_EXISTING_ACTIVITIES, "Goal Activity Management");

        // ====================== Organization Fit Survey Management Permissions ======================
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_CREATE_SURVEY_CATEGORY, "Organization Fit Survey Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_CREATE_SURVEY_QUESTION, "Organization Fit Survey Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_VIEW_SURVEY, "Organization Fit Survey Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_UPDATE_SURVEY_CATEGORY, "Organization Fit Survey Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_UPDATE_SURVEY_QUESTION, "Organization Fit Survey Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_DELETE_SURVEY_QUESTION, "Organization Fit Survey Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_REVIEW_ORGFIT_CATEGORY, "Organization Fit Survey Management");

        // ====================== Performance Tracking and Reporting Permissions ======================
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_VIEW_ALL_PERFORMANCE_SCORES, "Performance Tracking and Reporting");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_VIEW_MANAGER_PERFORMANCE_SCORES, "Performance Tracking and Reporting");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_VIEW_SELF_PERFORMANCE_SCORE, "Performance Tracking and Reporting");


        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_CREATE_REWARD, "Reward Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_READ_VIEW_REWARD, "Reward Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_UPDATE_REWARD, "Reward Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_DELETE_REWARD, "Reward Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_VIEW_SELF_REWARDS, "Reward Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_VIEW_MANAGER_REWARDS, "Reward Management");


        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_CREATE_PIP, "PIP Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_READ_VIEW_PIP, "PIP Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_UPDATE_PIP, "PIP Management");
        PERMISSION_CATEGORY_MAP.put(PermissionConstants.PERM_DELETE_PIP, "PIP Management");

    }

    private Long roleId;
    private Role selectedRole;
    private PermissionService permissionService;
    private RoleService roleService;

    private Map<String, List<Permission>> categorizedPermissions = new LinkedHashMap<>();
    private Set<Permission> selectedPermissions = new HashSet<>();

    @PostConstruct
    public void init() {
        this.permissionService = ApplicationContextProvider.getBean(PermissionService.class);
        this.roleService = ApplicationContextProvider.getBean(RoleService.class);

        List<Permission> allPermissions = permissionService.getPermissions();
        if (allPermissions != null) {
            categorizePermissions(allPermissions);
        }
    }

    private void categorizePermissions(List<Permission> permissions) {
        for (Permission perm : permissions) {
            String category = PERMISSION_CATEGORY_MAP.getOrDefault(perm.getName(), "General");
            categorizedPermissions.computeIfAbsent(category, k -> new ArrayList<>()).add(perm);
            System.out.println("categorized permissions" + categorizedPermissions.size());
        }
    }


    public void persist() {
        try {
            System.out.println("Selected role is: " +selectedRole );
            if (selectedRole != null) {
                System.out.println("Selected permissions is: " + selectedPermissions.size() );
                selectedRole.setPermissions(selectedPermissions);
                roleService.saveRole(selectedRole);
                UiUtils.showMessageBox("Success!", "Permissions assigned to role successfully.");
            } else {
                UiUtils.showMessageBox("Error", "Please select a role.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            UiUtils.showMessageBox("Action Failed!", e.getMessage());
        }
    }

    public String redirectToRolesView() throws IOException {
        return HyperLinks.ROLES_VIEW;
    }

    public void display(){
        for (Permission permission : selectedPermissions){
            System.out.println("Permissions are: " +permission.getName());
        }
    }
}
