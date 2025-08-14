package org.pahappa.systems.kpiTracker.views.teams;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import org.apache.commons.lang3.StringUtils;
import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.models.team.Team;
import org.pahappa.systems.kpiTracker.models.department.Department;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashSet;

@ManagedBean(name = "teamView")
@ViewScoped
@ViewPath(path = HyperLinks.TEAMS_VIEW)
@Secured({PermissionConstants.PERM_VIEW_TEAMS})
public class TeamView extends WebFormView<Team, TeamView, TeamView> implements Serializable {

    private static final long serialVersionUID = 1L;

    private LazyDataModel<Team> teamLazyDataModel;
    private List<User> users;
    private String searchTerm;
    private Team selectedTeam;
    private Team selectedTeamForDetails;
    private Team newTeam;
    private User selectedMember;
    private Team selectedTeamForAssignment;
    private Department selectedDepartment;
    private List<Team> departmentTeams;
    private List<User> membersWithoutTeam;
    private List<User> availableUsers;
    private List<User> selectedMembers = new ArrayList<>();

    private TeamService teamService;
    private UserService userService;
    private DepartmentService departmentService;

    @PostConstruct
    public void init() {
        try {
            this.userService = ApplicationContextProvider.getBean(UserService.class);
            this.teamService = ApplicationContextProvider.getBean(TeamService.class);
            this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
            this.users = userService.getUsers();
            this.availableUsers = new ArrayList<>(users);
            loadTeams();
            loadInitialData();
        } catch (OperationFailedException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to load data."));
        }
    }

    public void loadInitialData() {
        // Load department teams and members without team
        if (selectedDepartment != null) {
            loadDepartmentData();
        }
    }

    public void loadDepartmentData() {
        try {
            // Load teams for the selected department
            Search search = new Search();
            search.addFilterEqual("department", selectedDepartment);
            this.departmentTeams = teamService.getInstances(search, 0, 0);

            // Load members without team in this department
            loadMembersWithoutTeam();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to load department data."));
        }
    }

    public void loadMembersWithoutTeam() {
        try {
            this.membersWithoutTeam = teamService.getUsersWithoutTeamInDepartment(selectedDepartment);
        } catch (Exception e) {
            this.membersWithoutTeam = new ArrayList<>();
        }
    }

    public void loadTeams() {
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

    public void openNewTeamDialog() {
        this.newTeam = new Team();
        this.newTeam.setDepartment(selectedDepartment);
        this.selectedMembers.clear();
    }

    public void openEditDialog() {
    }

    public void openAssignDialog(User member) {
        this.selectedMember = member;
        this.selectedTeamForAssignment = null;
    }

    public void viewTeamDetails(Team team) {
        this.selectedTeamForDetails = team;
    }

    public String backToTeams() {
        return HyperLinks.TEAMS_VIEW + "?faces-redirect=true";
    }

    public void saveNewTeam() {
        try {
            newTeam.setMembers(new HashSet<>(selectedMembers));
            this.teamService.saveInstance(newTeam);

            loadDepartmentData();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Team created successfully."));
        } catch (ValidationFailedException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", e.getMessage()));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occurred while creating team."));
        }
    }

    public void updateTeam() {
        try {
            this.teamService.saveInstance(selectedTeam);
            loadDepartmentData();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Team updated successfully."));
        } catch (ValidationFailedException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", e.getMessage()));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occurred while updating team."));
        }
    }

    public void assignMemberToTeam() {
        try {
            if (selectedTeamForAssignment != null && selectedMember != null) {
                selectedTeamForAssignment.getMembers().add(selectedMember);
                teamService.saveInstance(selectedTeamForAssignment);

                loadDepartmentData();

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                                selectedMember.getFullName() + " assigned to " + selectedTeamForAssignment.getTeamName()));
            }
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to assign member to team."));
        }
    }

    public void removeMemberFromTeam(User member) {
        try {
            if (selectedTeamForDetails != null) {
                selectedTeamForDetails.getMembers().remove(member);
                teamService.saveInstance(selectedTeamForDetails);
                loadDepartmentData();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                                member.getFullName() + " removed from team."));
            }
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to remove member from team."));
        }
    }

    public void saveTeamChanges() {
        try {
            teamService.saveInstance(selectedTeamForDetails);
            loadDepartmentData();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Team changes saved successfully."));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to save team changes."));
        }
    }

    public int getMemberIndex(User member) {
        if (selectedTeamForDetails != null && selectedTeamForDetails.getMembers() != null) {
            return new ArrayList<>(selectedTeamForDetails.getMembers()).indexOf(member) + 1;
        }
        return 0;
    }

    @Override
    public void beanInit() {
        resetBean();
    }

    @Override
    public void pageLoadInit() {
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

    public void deleteTeam(Team team) {
        delete(team);
        loadDepartmentData();

    }

    public void resetBean() {
        super.model = new Team();
    }

    public LazyDataModel<Team> getTeamLazyDataModel() {
        return teamLazyDataModel;
    }

    public void setTeamLazyDataModel(LazyDataModel<Team> teamLazyDataModel) {
        this.teamLazyDataModel = teamLazyDataModel;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public Team getSelectedTeam() {
        return selectedTeam;
    }

    public void setSelectedTeam(Team selectedTeam) {
        this.selectedTeam = selectedTeam;
    }

    public Team getSelectedTeamForDetails() {
        return selectedTeamForDetails;
    }

    public void setSelectedTeamForDetails(Team selectedTeamForDetails) {
        this.selectedTeamForDetails = selectedTeamForDetails;
    }

    public Team getNewTeam() {
        return newTeam;
    }

    public void setNewTeam(Team newTeam) {
        this.newTeam = newTeam;
    }

    public User getSelectedMember() {
        return selectedMember;
    }

    public void setSelectedMember(User selectedMember) {
        this.selectedMember = selectedMember;
    }

    public Team getSelectedTeamForAssignment() {
        return selectedTeamForAssignment;
    }

    public void setSelectedTeamForAssignment(Team selectedTeamForAssignment) {
        this.selectedTeamForAssignment = selectedTeamForAssignment;
    }

    public Department getSelectedDepartment() {
        return selectedDepartment;
    }

    public void setSelectedDepartment(Department selectedDepartment) {
        this.selectedDepartment = selectedDepartment;
    }

    public List<Team> getDepartmentTeams() {
        return departmentTeams;
    }

    public void setDepartmentTeams(List<Team> departmentTeams) {
        this.departmentTeams = departmentTeams;
    }

    public List<User> getMembersWithoutTeam() {
        return membersWithoutTeam;
    }

    public void setMembersWithoutTeam(List<User> membersWithoutTeam) {
        this.membersWithoutTeam = membersWithoutTeam;
    }

    public List<User> getAvailableUsers() {
        return availableUsers;
    }

    public void setAvailableUsers(List<User> availableUsers) {
        this.availableUsers = availableUsers;
    }

    public List<User> getSelectedMembers() {
        return selectedMembers;
    }

    public void setSelectedMembers(List<User> selectedMembers) {
        this.selectedMembers = selectedMembers;
    }

    public TeamService getTeamService() {
        return teamService;
    }

    public void setTeamService(TeamService teamService) {
        this.teamService = teamService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public DepartmentService getDepartmentService() {
        return departmentService;
    }

    public void setDepartmentService(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

}