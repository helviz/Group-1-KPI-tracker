package org.pahappa.systems.kpiTracker.models.security;

public final class PermissionConstants {
    private PermissionConstants() {
    }

    // ====================== General System Permissions ======================
    @SystemPermission(name = "Api User", description = "Has a role for API users.")
    public static final String PERM_API_USER = "Api User";

    @SystemPermission(name = "Web Access", description = "A general web access permission required when migrating roles.")
    public static final String PERM_WEB_ACCESS = "Web Access";

    // ====================== User Management Permissions ======================
    @SystemPermission(name = "Create User", description = "Allows creating new users. Department Leads with this permission can only create users within their own department.")
    public static final String PERM_CREATE_USERS = "Create User";

    @SystemPermission(name = "Read/View Users", description = "Allows viewing users.")
    public static final String PERM_READ_VIEW_USERS = "Read/View Users";

    @SystemPermission(name = "Update User", description = "Allows modifying user attributes.")
    public static final String PERM_UPDATE_USER = "Update User";

    @SystemPermission(name = "Deactivate/Reactivate User", description = "Allows deactivating or reactivating user accounts. A user cannot be permanently deleted, only deactivated.")
    public static final String PERM_DEACTIVATE_REACTIVATE_USER = "Deactivate/Reactivate User";

    @SystemPermission(name = "Bulk Import Users", description = "Allows importing multiple users via a CSV template.")
    public static final String PERM_BULK_IMPORT_USERS = "Bulk Import Users";


    // ====================== Role and Permission Management Permissions ======================
    @SystemPermission(name = "Create Role", description = "Allows creating a new role.")
    public static final String PERM_CREATE_ROLE = "Create Role";

    @SystemPermission(name = "Read/View Roles", description = "Allows reading/viewing roles.")
    public static final String PERM_READ_VIEW_ROLES = "Read/View Roles";

    @SystemPermission(name = "Update Role", description = "Allows modifying an existing role.")
    public static final String PERM_UPDATE_ROLE = "Update Role";

    @SystemPermission(name = "Delete Role", description = "Allows deleting a role.")
    public static final String PERM_DELETE_ROLE = "Delete Role";

    @SystemPermission(name = "Assign Roles to User", description = "Allows assigning roles to users.")
    public static final String PERM_ASSIGN_ROLES_TO_USER = "Assign Roles to User";


    // ====================== Department Management Permissions ======================
    @SystemPermission(name = "Create Department", description = "Allows creating new departments.")
    public static final String PERM_CREATE_DEPARTMENT = "Create Department";

    @SystemPermission(name = "View Departments", description = "Allows reading/viewing department information.")
    public static final String PERM_VIEW_DEPARTMENTS = "View Departments";

    @SystemPermission(name = "Update Department", description = "Allows modifying department details.")
    public static final String PERM_UPDATE_DEPARTMENT = "Update Department";

    @SystemPermission(name = "Delete Department", description = "Allows soft deleting a department by setting its IsActive status to false.")
    public static final String PERM_DELETE_DEPARTMENT = "Delete Department";


    // ====================== Team Management Permissions ======================
    @SystemPermission(name = "Create Team", description = "Allows creating new organizational teams.")
    public static final String PERM_CREATE_TEAM = "Create Team";

    @SystemPermission(name = "Read/View Team", description = "Allows viewing team information.")
    public static final String PERM_READ_VIEW_TEAM = "Read/View Team";

    @SystemPermission(name = "Update Team", description = "Allows updating team details such as name, description, lead, and active status.")
    public static final String PERM_UPDATE_TEAM = "Update Team";

    @SystemPermission(name = "Deactivate Team", description = "Allows marking teams as inactive (IsActive = FALSE).")
    public static final String PERM_DEACTIVATE_TEAM = "Deactivate Team";

    @SystemPermission(name = "Delete Team", description = "Allows deleting teams, only if no active employees, goals, or KPIs are linked.")
    public static final String PERM_DELETE_TEAM = "Delete Team";

    @SystemPermission(name = "View Teams Page", description = "Specifically allows viewing the teams page.")
    public static final String PERM_VIEW_TEAMS_PAGE = "View Teams Page";


    // ====================== Goal Management Permissions ======================
    @SystemPermission(name = "Add Goal", description = "Allows defining new organizational goals.")
    public static final String PERM_ADD_GOAL = "Add Goal";

    @SystemPermission(name = "View Goal", description = "Allows viewing goal details.")
    public static final String PERM_VIEW_GOAL = "View Goal";

    @SystemPermission(name = "Update Goal", description = "Allows modifying existing goals.")
    public static final String PERM_UPDATE_GOAL = "Update Goal";

    @SystemPermission(name = "Delete Goal", description = "Allows deleting goals (soft delete by setting isActive to false). Deletion may be restricted if a goal has linked child goals.")
    public static final String PERM_DELETE_GOAL = "Delete Goal";

    @SystemPermission(name = "Approve Goal", description = "Allows users to approve a goal.")
    public static final String PERM_APPROVE_GOAL = "Approve Goal";

    @SystemPermission(name = "Request Changes for Goal", description = "Allows users to request changes for a goal.")
    public static final String PERM_REQUEST_CHANGES_FOR_GOAL = "Request Changes for Goal";

    @SystemPermission(name = "Reject Goal", description = "Allows users to reject a goal.")
    public static final String PERM_REJECT_GOAL = "Reject Goal";


    // ====================== Key Performance Indicator (KPI) Management Permissions ======================
    @SystemPermission(name = "Create KPIs", description = "Allows creating KPIs for goals.")
    public static final String PERM_CREATE_KPIS = "Create KPIs";

    @SystemPermission(name = "View KPIs", description = "Allows viewing KPIs.")
    public static final String PERM_VIEW_KPIS = "View KPIs";

    @SystemPermission(name = "Update KPIs", description = "Allows updating KPIs, including their progress.")
    public static final String PERM_UPDATE_KPIS = "Update KPIs";

    @SystemPermission(name = "Delete KPIs", description = "Allows deleting KPIs.")
    public static final String PERM_DELETE_KPIS = "Delete KPIs";


    // ====================== Goal Activity Management Permissions ======================
    @SystemPermission(name = "Attach New Activities", description = "Allows attaching new activities to a goal.")
    public static final String PERM_ATTACH_NEW_ACTIVITIES = "Attach New Activities";

    @SystemPermission(name = "Edit Existing Activities", description = "Allows editing existing activities.")
    public static final String PERM_EDIT_EXISTING_ACTIVITIES = "Edit Existing Activities";

    @SystemPermission(name = "Delete Existing Activities", description = "Allows deleting existing activities.")
    public static final String PERM_DELETE_EXISTING_ACTIVITIES = "Delete Existing Activities";


    // ====================== Organization Fit Survey Management Permissions ======================
    @SystemPermission(name = "Create Survey Category", description = "Allows users (e.g., HR_MANAGER, ADMIN roles) to create new survey categories.")
    public static final String PERM_CREATE_SURVEY_CATEGORY = "Create Survey Category";

    @SystemPermission(name = "Create Survey Question", description = "Allows users to create new questions within categories.")
    public static final String PERM_CREATE_SURVEY_QUESTION = "Create Survey Question";

    @SystemPermission(name = "View Survey", description = "Allows users to view categories and questions within the survey.")
    public static final String PERM_VIEW_SURVEY = "View Survey";

    @SystemPermission(name = "Update Survey Category", description = "Allows updating existing survey categories.")
    public static final String PERM_UPDATE_SURVEY_CATEGORY = "Update Survey Category";

    @SystemPermission(name = "Update Survey Question", description = "Allows updating existing survey questions.")
    public static final String PERM_UPDATE_SURVEY_QUESTION = "Update Survey Question";

    @SystemPermission(name = "Delete Survey Question", description = "Allows soft deleting survey questions.")
    public static final String PERM_DELETE_SURVEY_QUESTION = "Delete Survey Question";

    @SystemPermission(name = "Review OrgFit Category", description = "Required for users assigned to perform a review of an OrgFit Category.")
    public static final String PERM_REVIEW_ORGFIT_CATEGORY = "Review OrgFit Category";


    // ====================== Performance Tracking and Reporting Permissions ======================
    @SystemPermission(name = "View All Performance Scores", description = "Allows HR/Admins to view all performance scores across all levels (User, Team, Department, Organization).")
    public static final String PERM_VIEW_ALL_PERFORMANCE_SCORES = "View All Performance Scores";

    @SystemPermission(name = "View Manager Performance Scores", description = "Allows Managers to view scores for teams/departments they oversee.")
    public static final String PERM_VIEW_MANAGER_PERFORMANCE_SCORES = "View Manager Performance Scores";

    @SystemPermission(name = "View Self Performance Score", description = "Allows individual users to view their own performance scores.")
    public static final String PERM_VIEW_SELF_PERFORMANCE_SCORE = "View Self Performance Score";


    // ====================== Reward Management Permissions ======================
    @SystemPermission(name = "Create Reward", description = "Allows creating rewards for employees.")
    public static final String PERM_CREATE_REWARD = "Create Reward";

    @SystemPermission(name = "Read/View Reward", description = "Allows viewing rewards.")
    public static final String PERM_READ_VIEW_REWARD = "Read/View Reward";

    @SystemPermission(name = "Update Reward", description = "Allows updating reward details.")
    public static final String PERM_UPDATE_REWARD = "Update Reward";

    @SystemPermission(name = "Delete Reward", description = "Allows soft deleting rewards.")
    public static final String PERM_DELETE_REWARD = "Delete Reward";

    @SystemPermission(name = "View Self Rewards", description = "Employees can view their own rewards.")
    public static final String PERM_VIEW_SELF_REWARDS = "View Self Rewards";

    @SystemPermission(name = "View Manager Rewards", description = "Supervisors/Leads can view rewards for their direct reports/teams/departments.")
    public static final String PERM_VIEW_MANAGER_REWARDS = "View Manager Rewards";


    // ====================== PIP (Performance Improvement Plan) Management Permissions ======================
    @SystemPermission(name = "Create PIP", description = "Allows initiating a Performance Improvement Plan.")
    public static final String PERM_CREATE_PIP = "Create PIP";

    @SystemPermission(name = "Read/View PIP", description = "Allows viewing PIPs.")
    public static final String PERM_READ_VIEW_PIP = "Read/View PIP";

    @SystemPermission(name = "Update PIP", description = "Allows updating PIP progress or status.")
    public static final String PERM_UPDATE_PIP = "Update PIP";

    @SystemPermission(name = "Delete PIP", description = "Allows deleting a PIP.")
    public static final String PERM_DELETE_PIP = "Delete PIP";
}