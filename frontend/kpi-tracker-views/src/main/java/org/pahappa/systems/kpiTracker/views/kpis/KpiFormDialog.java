package org.pahappa.systems.kpiTracker.views.kpis;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.KpiService;
import org.pahappa.systems.kpiTracker.core.services.OrganisationGoalService;
import org.pahappa.systems.kpiTracker.core.services.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.core.services.TeamGoalService;
import org.pahappa.systems.kpiTracker.core.services.IndividualGoalService;
import org.pahappa.systems.kpiTracker.models.goalMgt.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.IndividualGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.OrganisationGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.TeamGoal;
import org.pahappa.systems.kpiTracker.models.kpi.KPI;
import org.pahappa.systems.kpiTracker.constants.KpiType;

import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "kpiFormDialog", eager = true)
@SessionScoped
@Getter
@Setter
@ViewPath(path = HyperLinks.KPI_FORM_DIALOG)
public class KpiFormDialog extends DialogForm<KPI> implements Serializable {

    private static final long serialVersionUID = 1L;

    private KpiService kpiService;
    private IndividualGoalService individualGoalService;
    private TeamGoalService teamGoalService;
    private DepartmentGoalService departmentGoalService;
    private OrganisationGoalService organisationGoalService;

    private IndividualGoal selectedGoal;
    private List<OrganisationGoal> organisationGoals;
    private List<DepartmentGoal> departmentGoals;
    private List<TeamGoal> teamGoals;
    private List<IndividualGoal> individualGoals;

    private OrganisationGoal selectedOrganisationGoal;
    private DepartmentGoal selectedDepartmentGoal;
    private TeamGoal selectedTeamGoal;

    private KpiType[] kpiTypes;
    private boolean isDataResetWarning;
    private boolean edit;

    public KpiFormDialog() {
        super(HyperLinks.KPI_FORM_DIALOG, 700, 450);
    }

    @PostConstruct
    public void init() {
        try {
            this.kpiService = ApplicationContextProvider.getBean(KpiService.class);
            this.individualGoalService = ApplicationContextProvider.getBean(IndividualGoalService.class);
            this.teamGoalService = ApplicationContextProvider.getBean(TeamGoalService.class);
            this.departmentGoalService = ApplicationContextProvider.getBean(DepartmentGoalService.class);
            this.organisationGoalService = ApplicationContextProvider.getBean(OrganisationGoalService.class);
            this.kpiTypes = KpiType.values();
            loadGoalHierarchy();
            resetModal();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reset(IndividualGoal goal) {
        super.resetModal();
        this.selectedGoal = goal;
        super.model = new KPI();
        super.model.setGoal(this.selectedGoal);
        this.isDataResetWarning = false;
    }

    @Override
    public void persist() throws ValidationFailedException, OperationFailedException {
        this.kpiService.saveInstance(super.getModel());
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new KPI();
        this.selectedGoal = null;
        this.isDataResetWarning = false;
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        if (super.getModel() != null) {
            this.selectedGoal = super.getModel().getGoal();
        } else {
            super.model = new KPI();
        }
    }

    public void prepareForNewKpi() {
        this.resetModal();
        this.setModel(new KPI());
        this.setSelectedGoal(null);
        this.setEdit(false);
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
                selectedGoal = null;
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
                selectedGoal = null;
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
                selectedGoal = null;
            } else {
                individualGoals.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}