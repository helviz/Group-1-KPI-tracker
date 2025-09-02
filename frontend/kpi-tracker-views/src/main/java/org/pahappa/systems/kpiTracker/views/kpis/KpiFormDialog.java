package org.pahappa.systems.kpiTracker.views.kpis;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.KpiService;
import org.pahappa.systems.kpiTracker.models.goalMgt.Goal;
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

@ManagedBean(name = "kpiFormDialog", eager = true)
@SessionScoped
@Getter
@Setter
@ViewPath(path = HyperLinks.KPI_FORM_DIALOG)
public class KpiFormDialog extends DialogForm<KPI> implements Serializable {

    private static final long serialVersionUID = 1L;

    private KpiService kpiService;
    private Goal selectedGoal;
    private KpiType[] kpiTypes;
    private boolean isDataResetWarning;
    private boolean edit;

    public KpiFormDialog(){
        super(HyperLinks.KPI_FORM_DIALOG, 700, 450);
    }

    @PostConstruct
    public void init() {
        try{
            this.kpiService = ApplicationContextProvider.getBean(KpiService.class);
            this.kpiTypes = KpiType.values();
            resetModal();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reset(Goal goal) {
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
        }else{
            super.model = new KPI();
        }
    }

    public void prepareForNewKpi() {
        this.resetModal();
        this.setModel(new KPI());
        this.setSelectedGoal(null);
        this.setEdit(false);
    }
}