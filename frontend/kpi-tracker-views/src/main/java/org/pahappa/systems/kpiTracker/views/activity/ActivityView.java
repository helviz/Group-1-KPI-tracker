package org.pahappa.systems.kpiTracker.views.activity;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.ActivityService;
import org.pahappa.systems.kpiTracker.core.services.GoalService;
import org.pahappa.systems.kpiTracker.models.activity.Activity;
import org.pahappa.systems.kpiTracker.models.goalMgt.Goal;
import org.pahappa.systems.kpiTracker.models.security.PermissionConstants;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.utils.FacesUtils;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "activityView")
@SessionScoped
@Getter
@Setter
@ViewPath(path = HyperLinks.ACTIVITY_VIEW)
public class ActivityView extends PaginatedTableView<Activity, ActivityView, ActivityService> implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient ActivityService activityService;
    private transient GoalService goalService;


    private String goalId;
    private Goal goal;
    private List<Activity> activities;
    private Activity activityToDelete;

    @PostConstruct
    public void init() {
        this.activityService = ApplicationContextProvider.getBean(ActivityService.class);
        this.goalService = ApplicationContextProvider.getBean(GoalService.class);

        if (goalId != null && !goalId.isEmpty()) {
            this.goal = goalService.getInstanceByID(goalId);
            if (this.goal != null) {
                loadActivities();
            } else {
                FacesUtils.addError("Error", "Goal not found.");
            }
        }
    }

    @Override
    public void reloadFromDB(int i, int i1, Map<String, Object> map) throws Exception {

    }

    @Override
    public List<ExcelReport> getExcelReportModels() {
        return Collections.emptyList();
    }

    @Override
    public String getFileName() {
        return "";
    }

    public void loadActivities() {
        try {
            this.activities = activityService.getActivitiesForGoal(this.goal);
        } catch (ValidationFailedException e) {
            FacesUtils.addError("Error", "Failed to load activities: " + e.getMessage());
        }
    }

    public void deleteActivity() {
        try {
            activityService.deleteInstance(this.activityToDelete);
            this.activities.remove(this.activityToDelete); // Optimistic UI update
            this.activityToDelete = null;
            FacesUtils.addInfo("Success", "Activity was successfully deleted.");
        } catch (Exception e) {
            FacesUtils.addError("Error", "Failed to delete activity: " + e.getMessage());
        }
    }

    public boolean hasPermission(String permission) {
        return true;
    }

    public String getPermAttachNewActivities() {
        return PermissionConstants.PERM_ATTACH_NEW_ACTIVITIES;
    }

    public String getPermEditExistingActivities() {
        return PermissionConstants.PERM_EDIT_EXISTING_ACTIVITIES;
    }

    public String getPermDeleteExistingActivities() {
        return PermissionConstants.PERM_DELETE_EXISTING_ACTIVITIES;
    }

    @Override
    public List<Activity> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        return Collections.emptyList();
    }
}