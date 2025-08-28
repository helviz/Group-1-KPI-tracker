package org.pahappa.systems.kpiTracker.views.goalMgt;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.GoalLevelService;
import org.pahappa.systems.kpiTracker.core.services.GoalPeriodService;
import org.pahappa.systems.kpiTracker.core.services.GoalService;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.models.goalMgt.Goal;
import org.pahappa.systems.kpiTracker.models.goalMgt.GoalLevel;
import org.pahappa.systems.kpiTracker.models.goalMgt.GoalPeriod;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.primefaces.PrimeFaces;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent; // IMPORTANT IMPORT
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@ManagedBean(name = "goalForm")
@SessionScoped
public class GoalForm extends DialogForm<Goal> {

    private static final long serialVersionUID = 1L;
    private transient GoalService goalService;
    private transient GoalLevelService goalLevelService;
    private transient GoalPeriodService goalPeriodService;
    private transient DepartmentService departmentService;

    private List<GoalLevel> availableGoalLevels;
    private List<Department> allDepartments;
    private List<GoalPeriod> goalPeriods;

    private List<Department> selectedDepartments = new ArrayList<>();
    private Goal parentGoalContext;
    private boolean isEditMode = false;

    // Dynamic form control properties
    private boolean showDepartmentSelection = false;
    private boolean showGoalPeriodSelection = true;
    private boolean goalLevelDisabled = false;
    private List<Goal> availableParentGoals;

    public GoalForm() {
        super("goalFormDialog", 800, 600);
    }

    @PostConstruct
    public void init() {
        try {
            this.goalService = ApplicationContextProvider.getBean(GoalService.class);
            this.goalLevelService = ApplicationContextProvider.getBean(GoalLevelService.class);
            this.goalPeriodService = ApplicationContextProvider.getBean(GoalPeriodService.class);
            this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);

            // Verify services are not null
            if (this.goalService == null) {
                System.err.println("GoalService is null");
            }
            if (this.goalLevelService == null) {
                System.err.println("GoalLevelService is null");
            }
            if (this.goalPeriodService == null) {
                System.err.println("GoalPeriodService is null");
            }
            if (this.departmentService == null) {
                System.err.println("DepartmentService is null");
            }

            this.allDepartments = departmentService.getAllDepartments();
            this.goalPeriods = goalPeriodService.getAllInstances();

            // Load all goal levels for debugging and initialize availableGoalLevels
            List<GoalLevel> allGoalLevels = goalLevelService.getAllInstances();
            this.availableGoalLevels = allGoalLevels != null ? allGoalLevels : new ArrayList<>();

            System.out.println("GoalForm initialized successfully");
            System.out
                    .println("All departments: " + (this.allDepartments != null ? this.allDepartments.size() : "null"));
            System.out.println("Goal periods: " + (this.goalPeriods != null ? this.goalPeriods.size() : "null"));
            System.out.println("All goal levels: " + (allGoalLevels != null ? allGoalLevels.size() : "null"));
            if (allGoalLevels != null) {
                for (GoalLevel level : allGoalLevels) {
                    System.out.println("Goal Level: " + level.getName());
                }
            }

        } catch (Exception e) {
            System.err.println("Error initializing GoalForm: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void persist() throws Exception {
        // ... (The persist logic remains the same)
        if (super.model.isDepartmentGoal()) {
            if (selectedDepartments.isEmpty()) {
                throw new Exception("You must select at least one department.");
            }
            super.model.getGoalDepartments().clear();
            selectedDepartments.forEach(dep -> super.model.addDepartment(dep, 0.0));
        } else if (this.parentGoalContext != null) {
            Goal parent = this.parentGoalContext;
            if (super.model.isTeamGoal() && parent.isDepartmentGoal()) {
                super.model.getGoalDepartments().clear();
                parent.getGoalDepartments().forEach(gd -> super.model.addDepartment(gd.getDepartment(), 0.0));
            } else if (super.model.isIndividualGoal()) {
                if (parent.isTeamGoal()) {
                    super.model.setTeam(parent.getTeam());
                    super.model.getGoalDepartments().clear();
                    parent.getGoalDepartments().forEach(gd -> super.model.addDepartment(gd.getDepartment(), 0.0));
                } else if (parent.isDepartmentGoal()) {
                    super.model.getGoalDepartments().clear();
                    parent.getGoalDepartments().forEach(gd -> super.model.addDepartment(gd.getDepartment(), 0.0));
                }
            }
        }
        this.goalService.saveInstance(super.model);
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new Goal();
        this.selectedDepartments.clear();
        this.parentGoalContext = null;
        this.isEditMode = false;

        // Reset dynamic form properties
        this.showDepartmentSelection = false;
        this.showGoalPeriodSelection = true;
        this.goalLevelDisabled = false;
        this.availableParentGoals = null;
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        if (super.model != null && super.model.getId() != null) {
            this.isEditMode = true;
            super.setName("Edit Goal: " + super.model.getGoalTitle());
            this.selectedDepartments.clear();

            // Pre-populate departments if it's a Department goal
            if (super.model.isDepartmentGoal()) {
                super.model.getGoalDepartments().forEach(gd -> this.selectedDepartments.add(gd.getDepartment()));
                this.showDepartmentSelection = true;
            } else {
                this.showDepartmentSelection = false;
            }

            // Disable goal level selection for editing
            this.availableGoalLevels = Collections.singletonList(super.model.getGoalLevel());
            this.goalLevelDisabled = true;
            this.showGoalPeriodSelection = true;
        }
    }

    // --- BUTTON ACTION METHODS ---

    public void prepareNewOrganizationalGoal(ActionEvent event) {
        try {
            // Check if services are initialized
            if (goalLevelService == null) {
                throw new RuntimeException("GoalLevelService is not initialized");
            }

            // Check if user is logged in
            if (SharedAppData.getLoggedInUser() == null) {
                throw new RuntimeException("No user is currently logged in");
            }

            resetModal();
            super.model = new Goal();
            super.setName("Create New Organization Goal");
            super.model.setOwner(SharedAppData.getLoggedInUser());

            // Load all goal levels first
            List<GoalLevel> allGoalLevels = goalLevelService.getAllInstances();
            System.out.println("All goal levels loaded: " + (allGoalLevels != null ? allGoalLevels.size() : "null"));

            if (allGoalLevels != null) {
                for (GoalLevel level : allGoalLevels) {
                    System.out.println("Available goal level: " + level.getName());
                }
            }

            // Filter goal levels to show only Organization
            this.availableGoalLevels = allGoalLevels.stream()
                    .filter(level -> "Organization".equalsIgnoreCase(level.getName()))
                    .collect(Collectors.toList());

            System.out.println("Filtered goal levels for Organization: " + this.availableGoalLevels.size());

            if (!availableGoalLevels.isEmpty()) {
                super.model.setGoalLevel(availableGoalLevels.get(0));
                System.out.println("Set goal level to: " + availableGoalLevels.get(0).getName());
            } else {
                System.out.println("No Organization goal level found! Showing all available levels.");
                // Fallback: show all available goal levels
                this.availableGoalLevels = allGoalLevels;
                if (!availableGoalLevels.isEmpty()) {
                    super.model.setGoalLevel(availableGoalLevels.get(0));
                    System.out.println("Set goal level to: " + availableGoalLevels.get(0).getName());
                }
            }

            // Set dynamic form properties
            this.showGoalPeriodSelection = true;
            this.showDepartmentSelection = false;
            this.goalLevelDisabled = false;

            // Call the show method
            show(event);

        } catch (Exception e) {
            // Log the error and show a user-friendly message
            System.err.println("Error in prepareNewOrganizationalGoal: " + e.getMessage());
            e.printStackTrace();

            // You can add a FacesMessage here to show the error to the user
            // FacesContext.getCurrentInstance().addMessage(null,
            // new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void prepareNewChildGoal(ActionEvent event) {
        // Retrieve the parent goal passed as an attribute from the button
        this.parentGoalContext = (Goal) event.getComponent().getAttributes().get("parentGoal");

        resetModal();
        super.model = new Goal();
        super.setName("Create New Child Goal");
        super.model.setParentGoal(this.parentGoalContext);
        super.model.setOwner(SharedAppData.getLoggedInUser());

        // Inherit goal period from parent (user cannot change this)
        super.model.setGoalPeriod(this.parentGoalContext.getGoalPeriod());
        this.showGoalPeriodSelection = false;

        loadValidChildLevels(this.parentGoalContext.getGoalLevel().getName());

        // Load available parent goals for selection
        loadAvailableParentGoals(this.parentGoalContext.getGoalLevel().getName());

        // Call the show method
        show(event);
    }

    private void loadValidChildLevels(String parentLevelName) {
        List<String> validChildLevelNames = new ArrayList<>();
        switch (parentLevelName.toLowerCase()) {
            case "organization":
                validChildLevelNames.add("Department");
                break;
            case "department":
                validChildLevelNames.addAll(Arrays.asList("Team", "Individual"));
                break;
            case "team":
                validChildLevelNames.add("Individual");
                break;
        }

        if (validChildLevelNames.isEmpty()) {
            this.availableGoalLevels = Collections.emptyList();
        } else {
            // FIXED: Replaced incorrect method call
            this.availableGoalLevels = goalLevelService.getAllInstances().stream()
                    .filter(level -> validChildLevelNames.contains(level.getName()))
                    .collect(Collectors.toList());
        }
    }

    /**
     * AJAX event handler for Goal Level dropdown
     * Shows/hides department selection based on level
     */
    public void onGoalLevelChange(ActionEvent event) {
        if (super.model != null && super.model.getGoalLevel() != null) {
            String levelName = super.model.getGoalLevel().getName();

            // Show department selection only for Department level goals
            this.showDepartmentSelection = "Department".equalsIgnoreCase(levelName);

            // Load available parent goals for the selected level
            if (!"Organization".equalsIgnoreCase(levelName)) {
                loadAvailableParentGoals(levelName);
            }
        }
    }

    /**
     * Load available parent goals for a given level
     */
    private void loadAvailableParentGoals(String goalLevelName) {
        try {
            String userId = SharedAppData.getLoggedInUser().getId();
            this.availableParentGoals = goalService.getAvailableParentGoals(goalLevelName, userId);
        } catch (Exception e) {
            this.availableParentGoals = Collections.emptyList();
        }
    }

    /**
     * Check if the form is in edit mode
     */
    public boolean isEditMode() {
        return this.isEditMode;
    }

    /**
     * Check if the form is in create mode
     */
    public boolean isCreateMode() {
        return super.model != null && super.model.getId() == null;
    }

    /**
     * Get the current goal level name
     */
    public String getCurrentGoalLevelName() {
        return super.model != null && super.model.getGoalLevel() != null ? super.model.getGoalLevel().getName() : "";
    }

    /**
     * Edit an existing goal
     */
    public void edit(ActionEvent event) {
        Goal goalToEdit = (Goal) event.getComponent().getAttributes().get("entity");
        if (goalToEdit != null) {
            super.setModel(goalToEdit);
            show(event);
        }
    }

    /**
     * Override the show method to use the correct path
     */
    @Override
    public void show(ActionEvent actionEvent) {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("modal", true);
        options.put("draggable", true);
        options.put("resizable", true);
        options.put("contentHeight", this.getHeight());
        options.put("contentWidth", this.getWidth());
        options.put("style", "");
        options.put("widgetVar", this.getName());
        options.put("id", this.getName());
        PrimeFaces.current().dialog().openDynamic(HyperLinks.GOAL_FORM, options, null);
    }
}