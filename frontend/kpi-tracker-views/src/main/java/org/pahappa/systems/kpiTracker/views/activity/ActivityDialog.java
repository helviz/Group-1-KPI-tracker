package org.pahappa.systems.kpiTracker.views.activity;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.ActivityService;
import org.pahappa.systems.kpiTracker.core.services.OrganisationGoalService;
import org.pahappa.systems.kpiTracker.core.services.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.core.services.TeamGoalService;
import org.pahappa.systems.kpiTracker.core.services.IndividualGoalService;
import org.pahappa.systems.kpiTracker.models.activity.Activity;
import org.pahappa.systems.kpiTracker.models.goalMgt.OrganisationGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.TeamGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.IndividualGoal;
import org.pahappa.systems.kpiTracker.constants.ActivityStatus;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.pahappa.systems.kpiTracker.views.dialogs.MessageComposer;
import org.pahappa.systems.kpiTracker.views.utils.FacesUtils;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.primefaces.PrimeFaces;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "activityFormDialog")
@SessionScoped
@Getter
@Setter
@ViewPath(path = HyperLinks.ACTIVITY_FORM_DIALOG)
public class ActivityDialog extends DialogForm<Activity> implements Serializable {

    private static final long serialVersionUID = 1L;
    private transient ActivityService activityService;
    private transient IndividualGoalService individualGoalService;
    private transient TeamGoalService teamGoalService;
    private transient DepartmentGoalService departmentGoalService;
    private transient OrganisationGoalService organisationGoalService;

    private List<OrganisationGoal> organisationGoals;
    private List<DepartmentGoal> departmentGoals;
    private List<TeamGoal> teamGoals;
    private List<IndividualGoal> individualGoals;

    private OrganisationGoal selectedOrganisationGoal;
    private DepartmentGoal selectedDepartmentGoal;
    private TeamGoal selectedTeamGoal;
    private IndividualGoal selectedIndividualGoal;

    public ActivityDialog() {
        super(HyperLinks.ACTIVITY_FORM_DIALOG, 700, 500);
        this.activityService = ApplicationContextProvider.getBean(ActivityService.class);
        this.individualGoalService = ApplicationContextProvider.getBean(IndividualGoalService.class);
        this.teamGoalService = ApplicationContextProvider.getBean(TeamGoalService.class);
        this.departmentGoalService = ApplicationContextProvider.getBean(DepartmentGoalService.class);
        this.organisationGoalService = ApplicationContextProvider.getBean(OrganisationGoalService.class);
        loadGoalHierarchy();
        resetModal();
    }

    public void prepareNew(IndividualGoal goal) {
        if (goal == null) {
            FacesUtils.addError("Error", "A goal must be selected before adding an activity.");
            PrimeFaces.current().ajax().addCallbackParam("validationFailed", true);
            return;
        }

        super.model = new Activity();
        super.model.setGoal(goal);
        super.model.setStatus(ActivityStatus.NOT_STARTED);
    }

    @Override
    public void persist() {
        try {
            this.activityService.saveInstance(super.model);

        } catch (ValidationFailedException | OperationFailedException e) {
            MessageComposer.error("Error", e.getMessage());
        }
    }

    public ActivityStatus[] getActivityStatuses() {
        return ActivityStatus.values();
    }

    // Goal hierarchy loading methods
    public void loadGoalHierarchy() {
        try {
            organisationGoals = organisationGoalService.findAllActive();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onOrganisationGoalChange() {
        try {
            if (selectedOrganisationGoal != null) {
                departmentGoals = departmentGoalService.findByParentGoal(selectedOrganisationGoal);
                selectedDepartmentGoal = null;
                selectedTeamGoal = null;
                selectedIndividualGoal = null;
            } else {
                departmentGoals.clear();
                teamGoals.clear();
                individualGoals.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDepartmentGoalChange() {
        try {
            if (selectedDepartmentGoal != null) {
                teamGoals = teamGoalService.findByParentGoal(selectedDepartmentGoal);
                selectedTeamGoal = null;
                selectedIndividualGoal = null;
            } else {
                teamGoals.clear();
                individualGoals.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onTeamGoalChange() {
        try {
            if (selectedTeamGoal != null) {
                individualGoals = individualGoalService.findByParentGoal(selectedTeamGoal);
                selectedIndividualGoal = null;
            } else {
                individualGoals.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new Activity();
    }

    @Override
    public void save() {
        persist();
    }

    // Getters and setters for goal hierarchy
    public List<OrganisationGoal> getOrganisationGoals() {
        return organisationGoals;
    }

    public void setOrganisationGoals(List<OrganisationGoal> organisationGoals) {
        this.organisationGoals = organisationGoals;
    }

    public List<DepartmentGoal> getDepartmentGoals() {
        return departmentGoals;
    }

    public void setDepartmentGoals(List<DepartmentGoal> departmentGoals) {
        this.departmentGoals = departmentGoals;
    }

    public List<TeamGoal> getTeamGoals() {
        return teamGoals;
    }

    public void setTeamGoals(List<TeamGoal> teamGoals) {
        this.teamGoals = teamGoals;
    }

    public List<IndividualGoal> getIndividualGoals() {
        return individualGoals;
    }

    public void setIndividualGoals(List<IndividualGoal> individualGoals) {
        this.individualGoals = individualGoals;
    }

    public OrganisationGoal getSelectedOrganisationGoal() {
        return selectedOrganisationGoal;
    }

    public void setSelectedOrganisationGoal(OrganisationGoal selectedOrganisationGoal) {
        this.selectedOrganisationGoal = selectedOrganisationGoal;
    }

    public DepartmentGoal getSelectedDepartmentGoal() {
        return selectedDepartmentGoal;
    }

    public void setSelectedDepartmentGoal(DepartmentGoal selectedDepartmentGoal) {
        this.selectedDepartmentGoal = selectedDepartmentGoal;
    }

    public TeamGoal getSelectedTeamGoal() {
        return selectedTeamGoal;
    }

    public void setSelectedTeamGoal(TeamGoal selectedTeamGoal) {
        this.selectedTeamGoal = selectedTeamGoal;
    }

    public IndividualGoal getSelectedIndividualGoal() {
        return selectedIndividualGoal;
    }

    public void setSelectedIndividualGoal(IndividualGoal selectedIndividualGoal) {
        this.selectedIndividualGoal = selectedIndividualGoal;
    }
}
