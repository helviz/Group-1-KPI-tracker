package org.pahappa.systems.kpiTracker.views.goalMgt;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.OrganisationGoalService;
import org.pahappa.systems.kpiTracker.models.goalMgt.OrganisationGoal;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.math.BigDecimal;
import java.util.List;

@ManagedBean(name = "organisationGoalFormDialog", eager = true)
@Getter
@Setter
@ViewPath(path = HyperLinks.ORGANISATION_GOAL_FORM_DIALOG)
@SessionScoped
public class OrganisationGoalFormDialog extends DialogForm<OrganisationGoal> {

    private static final long serialVersionUID = 1L;
    private OrganisationGoalService organisationGoalService;
    private UserService userService;

    private List<User> availableUsers;

    @PostConstruct
    public void init() {
        try {
            this.organisationGoalService = ApplicationContextProvider.getBean(OrganisationGoalService.class);
            this.userService = ApplicationContextProvider.getBean(UserService.class);
            this.availableUsers = this.userService.getUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public OrganisationGoalFormDialog() {
        super(HyperLinks.ORGANISATION_GOAL_FORM_DIALOG, 700, 500);
    }

    @Override
    public void persist() throws ValidationFailedException, OperationFailedException {
        this.organisationGoalService.saveInstance(super.model);
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new OrganisationGoal();
        // Set default values
        super.model.setProgress(BigDecimal.ZERO);
        super.model.setEvaluationTarget(new BigDecimal("100.0"));
        super.model.setContributionToParent(new BigDecimal("100.0"));
        super.model.setIsActive(true);
    }

    @Override
    public void setFormProperties() {
        if (super.model == null) {
            super.model = new OrganisationGoal();
            super.model.setProgress(BigDecimal.ZERO);
            super.model.setEvaluationTarget(new BigDecimal("100.0"));
            super.model.setContributionToParent(new BigDecimal("100.0"));
            super.model.setIsActive(true);
        }
    }

    public void loadGoal(OrganisationGoal goal) {
        if (goal != null) {
            super.model = goal;
        }
    }

    public List<User> getAvailableUsers() {
        return availableUsers;
    }

    public void setAvailableUsers(List<User> availableUsers) {
        this.availableUsers = availableUsers;
    }
}
