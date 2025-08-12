package org.pahappa.systems.kpiTracker.views.teams;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import org.apache.commons.lang3.StringUtils;
import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.models.team.Team;
import org.pahappa.systems.kpiTracker.models.security.PermissionConstants;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.client.views.presenters.WebFormView;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "teamView")
@ViewScoped
@ViewPath(path = HyperLinks.TEAMS_VIEW)
@Secured({PermissionConstants.PERM_VIEW_TEAMS})
public class TeamView extends WebFormView<Team, TeamView, TeamView> implements Serializable {

    private static final long serialVersionUID = 1L;
    private LazyDataModel<Team> teamLazyDataModel;
    private List<User> users;
    private String searchTerm;


    private TeamService teamService;

    private UserService userService;

    @PostConstruct
    public void init() {
        try {
            this.userService = ApplicationContextProvider.getBean(UserService.class);
            this.teamService = ApplicationContextProvider.getBean(TeamService.class);
            this.users = userService.getUsers();
            loadTeams();
        } catch (OperationFailedException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to load users."));
        }
    }

    private void loadTeams() {
        this.teamLazyDataModel = new LazyDataModel<Team>() {
            private static final long serialVersionUID = 1L;

            @Override
            public List<Team> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
                Search search = new Search().setFirstResult(first).setMaxResults(pageSize);

                if (sortBy != null && !sortBy.isEmpty()) {
                    for (SortMeta meta : sortBy.values()) {
                        search.addSort(meta.getField(), meta.getOrder().equals(SortOrder.DESCENDING));
                    }
                }

                if (StringUtils.isNotBlank(searchTerm)) {
                    search.addFilterOr(
                            Filter.like("teamName", "%" + searchTerm + "%"),
                            Filter.like("description", "%" + searchTerm + "%")
                    );
                }

                super.setRowCount(teamService.countInstances(search));
                return teamService.getInstances(search, first, pageSize);
            }

            @Override
            public String getRowKey(Team team) {
                return team.getId();
            }

            @Override
            public Team getRowData(String rowKey) {
                return teamService.getInstanceByID(rowKey);
            }
        };
    }

    @Override
    public void beanInit() {
        resetBean();
    }

    @Override
    public void pageLoadInit() {
        // Runs on every page load
    }

    @Override
    public void persist() {
        try {
            this.teamService.saveInstance(super.model);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Team saved successfully."));
            super.resetModal();
        } catch (ValidationFailedException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", e.getMessage()));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occurred while saving. Please try again."));
        }
    }

    public void delete(Team team) {
        try {
            this.teamService.deleteInstance(team);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Team deleted successfully."));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ex.getMessage()));
        }
    }

    public void resetBean() {
        super.model = new Team();
    }

    public LazyDataModel<Team> getTeamLazyDataModel() {
        return teamLazyDataModel;
    }

    public List<User> getUsers() {
        return users;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
}