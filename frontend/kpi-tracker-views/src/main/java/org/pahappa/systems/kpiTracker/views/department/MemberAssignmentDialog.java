package org.pahappa.systems.kpiTracker.views.department;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.core.services.AssignedUserService;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.models.team.Team;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.pahappa.systems.kpiTracker.views.dialogs.MessageComposer;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "memberAssignmentDialog")
@Getter
@Setter
@SessionScoped
public class MemberAssignmentDialog extends DialogForm<User> {

    private TeamService teamService;
    private AssignedUserService assignedUserService;

    private User selectedMember;
    private Department selectedDepartment;
    private Team selectedTeam;
    private List<Team> availableTeams;

    public MemberAssignmentDialog() {
        super("memberAssignmentDialog", 500, 400);
    }

    @PostConstruct
    public void init() {
        try {
            this.teamService = ApplicationContextProvider.getBean(TeamService.class);
            this.assignedUserService = ApplicationContextProvider.getBean(AssignedUserService.class);
        } catch (Exception e) {
            System.err.println("Error initializing MemberAssignmentDialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void show(ActionEvent actionEvent) {
        if (selectedDepartment != null) {
            loadAvailableTeams();
        }
        super.show(actionEvent);
    }

    /**
     * Load available teams for the selected department
     */
    private void loadAvailableTeams() {
        try {
            if (selectedDepartment != null) {
                // Get all teams in the department
                com.googlecode.genericdao.search.Search search = new com.googlecode.genericdao.search.Search();
                search.addFilterEqual("department", selectedDepartment);
                search.addFilterEqual("recordStatus", org.sers.webutils.model.RecordStatus.ACTIVE);
                this.availableTeams = teamService.getInstances(search, 0, 1000);
            } else {
                this.availableTeams = new ArrayList<>();
            }
        } catch (Exception e) {
            System.err.println("Error loading available teams: " + e.getMessage());
            this.availableTeams = new ArrayList<>();
        }
    }

    /**
     * Assign the selected member to the selected team
     */
    public void assignMemberToTeam() {
        try {
            if (selectedMember == null) {
                MessageComposer.error("Error", "No member selected.");
                return;
            }

            if (selectedTeam == null) {
                MessageComposer.error("Error", "Please select a team.");
                return;
            }

            // Add member to team
            selectedTeam.getMembers().add(selectedMember);
            teamService.saveInstance(selectedTeam);

            MessageComposer.info("Success",
                    "Member '" + selectedMember.getFullName() + "' has been assigned to team '"
                            + selectedTeam.getTeamName() + "'.");

            // Reset and hide dialog
            resetModal();
            hide();

        } catch (Exception e) {
            MessageComposer.error("Assignment Failed", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void persist() throws Exception {
        // This method is not used for this dialog
    }

    @Override
    public void resetModal() {
        super.resetModal();
        this.selectedMember = null;
        this.selectedDepartment = null;
        this.selectedTeam = null;
        this.availableTeams = new ArrayList<>();
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        // Not needed for this dialog
    }
}
