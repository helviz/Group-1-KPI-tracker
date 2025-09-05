package org.pahappa.systems.kpiTracker.views.users;

import com.google.common.collect.Sets;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.CustomService;
import org.pahappa.systems.kpiTracker.core.services.impl.CustomServiceImpl;
import org.pahappa.systems.kpiTracker.models.security.PermissionConstants;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.Permission;
import org.sers.webutils.model.security.Role;
import org.sers.webutils.model.utils.SearchField;
import org.sers.webutils.model.utils.SortField;
import org.sers.webutils.server.core.service.PermissionService;
import org.sers.webutils.server.core.service.RoleService;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.CustomLogger;
import org.sers.webutils.server.shared.CustomLogger.LogSeverity;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import java.io.IOException;
import java.util.*;

@ManagedBean(name = "rolesView")
@Getter
@Setter
@SessionScoped
@ViewPath(path = HyperLinks.ROLES_VIEW)
public class RolesView extends PaginatedTableView<Role, RolesView, RolesView> {

	private static final long serialVersionUID = 1L;
	private RoleService roleService;
	private PermissionService permissionService;
	private CustomService customService;

	private String searchTerm;
	private Role selectedRole;
	private Set<Permission> permissionsList = new HashSet<Permission>();
	private Set<Permission> selectedPermissions = new HashSet<Permission>(); // Changed from selectedPermissionsList
	private Search search;
	private Date startDate, endDate;
	private List<SearchField> searchFields;
	private SortField selectedSortField = new SortField("dateCreated", "dateCreated", true);

	// New fields from PermissionsView
	private static final Map<String, String> PERMISSION_CATEGORY_MAP = new HashMap<>();
	private List<SelectItem> groupedPermissions;

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

	@PostConstruct
	public void init() {
		this.roleService = ApplicationContextProvider.getBean(RoleService.class);
		this.permissionService = ApplicationContextProvider.getBean(PermissionService.class);
		this.customService = ApplicationContextProvider.getBean(CustomService.class);

		// Removed permissionsList as it's not directly used for display in the new design
		// this.permissionsList = Sets.newHashSet(customService.getPermissions(new Search().addSortAsc("name"), 0, 0));

		this.searchFields = Arrays.asList(
				new SearchField[] { new SearchField("Name", "name"), new SearchField("Description", "description") });
		super.setMaximumresultsPerpage(10);
		reloadFilterReset();

		// Initialize grouped permissions for the display
		List<Permission> allPermissions = permissionService.getPermissions();
		if (allPermissions != null) {
			generateGroupedPermissions(allPermissions);
		}
	}

	private void generateGroupedPermissions(List<Permission> permissions) {
		Map<String, List<SelectItem>> categorizedItems = new LinkedHashMap<>();
		for (Permission perm : permissions) {
			String category = PERMISSION_CATEGORY_MAP.get(perm.getName());
			if (category == null) {
				category = "General";
			}
			if (!categorizedItems.containsKey(category)) {
				categorizedItems.put(category, new ArrayList<>());
			}
			categorizedItems.get(category).add(new SelectItem(perm, perm.getName()));
		}

		groupedPermissions = new ArrayList<>();
		for (Map.Entry<String, List<SelectItem>> entry : categorizedItems.entrySet()) {
			SelectItemGroup group = new SelectItemGroup(entry.getKey());
			group.setSelectItems(entry.getValue().toArray(new SelectItem[0]));
			groupedPermissions.add(group);
		}
	}

	@Override
	public List<Role> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
		return getDataModels();
	}

	@Override
	public void reloadFromDB(int offset, int limit, Map<String, Object> filters) {
		super.setDataModels(this.customService.getRoles(search, offset, limit));
	}

	@Override
	public void reloadFilterReset() {
		this.search = CustomServiceImpl.composeRoleSearch(searchFields, searchTerm, startDate, endDate,
				selectedSortField);
		super.setTotalRecords(customService.countRoles(search));
		CustomLogger.log(LogSeverity.LEVEL_DEBUG, "Total records " + super.getTotalRecords());
		try {
			super.reloadFilterReset();
		} catch (Exception e) {
			CustomLogger.log(LogSeverity.LEVEL_ERROR, e.getMessage());
		}
	}

	@Override
	public List<ExcelReport> getExcelReportModels() {
		return Collections.emptyList();
	}

	@Override
	public String getFileName() {
		return null;
	}

	public void saveSelectedRolePermissions() { // Renamed method
		try {
			if (selectedRole != null) {
				selectedRole.setPermissions(this.selectedPermissions);
				roleService.saveRole(this.selectedRole);
				UiUtils.showMessageBox("Action Success!", "Permissions assigned to role successfully.");
			} else {
				UiUtils.showMessageBox("Error", "Please select a role.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			UiUtils.showMessageBox("Action Failed!", e.getMessage());
		}
	}

	public void deleteSelectedRole(Role role) {
		try {
			customService.deleteRole(role);
			UiUtils.showMessageBox("Action Success!", "Role deleted");
			reloadFilterReset(); // Refresh the roles list
		} catch (Exception e) {
			e.printStackTrace();
			UiUtils.showMessageBox("Action Failed!", e.getMessage());
		}
	}

	public void loadSelectedRole(Role role) {
		if (role != null) {
			this.selectedRole = role;
			// Ensure selectedPermissions is not null
			this.selectedPermissions = role.getPermissions() != null ? new HashSet<>(role.getPermissions()) : new HashSet<>();
		} else {
			this.selectedPermissions = new HashSet<>();
			this.selectedRole = new Role();
		}
	}



	public void displaySelectedPermissions() {
		for (Permission permission : selectedPermissions) {
			System.out.println("Selected Permission: " + permission.getName());
		}
	}
}