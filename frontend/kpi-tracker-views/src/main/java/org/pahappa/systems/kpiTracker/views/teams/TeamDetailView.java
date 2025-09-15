package org.pahappa.systems.kpiTracker.views.teams;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.StaffService;
import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.models.team.Team;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "teamDetailView")
@Getter
@Setter
@ViewScoped
public class TeamDetailView {

    private TeamService teamService;
    private StaffService staffService;

    private Team team;
    private String teamId;
    private List<Staff> teamMembers;

    @PostConstruct
    public void init() {
        try {
            this.teamService = ApplicationContextProvider.getBean(TeamService.class);
            this.staffService = ApplicationContextProvider.getBean(StaffService.class);

            javax.faces.context.FacesContext facesContext = javax.faces.context.FacesContext.getCurrentInstance();
            if (facesContext != null) {
                Object flashTeamId = facesContext.getExternalContext().getFlash().get("selectedTeamId");
                if (flashTeamId != null) {
                    this.teamId = flashTeamId.toString();
                } else {
                    String paramTeamId = facesContext.getExternalContext().getRequestParameterMap()
                            .get("teamId");
                    if (paramTeamId != null) {
                        this.teamId = paramTeamId;
                    }
                }
            }

            if (this.teamId != null && !this.teamId.trim().isEmpty()) {
                loadTeamDetails();
            }

        } catch (Exception e) {
            System.err.println("Error initializing TeamDetailView: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadTeamDetails() {
        try {
            this.team = teamService.getInstanceByID(this.teamId);
            if (this.team == null) {
                System.err.println("Team not found with ID: " + this.teamId);
                return;
            }
            this.teamMembers = staffService.getStaffByTeam(this.team);
        } catch (Exception e) {
            System.err.println("Error loading team details: " + e.getMessage());
            e.printStackTrace();
            this.teamMembers = new ArrayList<Staff>();
        }
    }

    public String navigateToDepartmentDetailFromTeam() {
        // If you want to go back to all teams, not just department-filtered teams
        return HyperLinks.DEPARTMENT_DETAIL_VIEW + "?faces-redirect=true";
    }

//    public String navigateToDepartmentDetailFromTeam() {
//        if (team != null && team.getDepartment() != null) {
//            javax.faces.context.FacesContext.getCurrentInstance()
//                    .getExternalContext().getFlash().put("selectedDepartmentId", team.getDepartment().getId());
//            return HyperLinks.DEPARTMENT_DETAIL_VIEW + "?faces-redirect=true";
//        }
//        return HyperLinks.DEPARTMENT_VIEW + "?faces-redirect=true"; // Fallback
//    }

    public boolean hasMembers() {
        return teamMembers != null && !teamMembers.isEmpty();
    }
}