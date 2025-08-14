package org.pahappa.systems.kpiTracker.models.user;

import org.pahappa.systems.kpiTracker.models.department.Department;
import org.pahappa.systems.kpiTracker.models.team.Team;
import org.sers.webutils.model.BaseEntity;
import org.sers.webutils.model.security.User;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "assigned_users")
public class AssignedUser extends BaseEntity {

    private User user;
    private Department department;
    private List<Team> assignedTeams;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "assigned_user_team", // join table name
            joinColumns = @JoinColumn(name = "assigned_user_id"), // FK to assigned_users
            inverseJoinColumns = @JoinColumn(name = "team_id")    // FK to teams
    )
    public List<Team> getAssignedTeams() {
        return assignedTeams;
    }

    public void setAssignedTeams(List<Team> assignedTeams) {
        this.assignedTeams = assignedTeams;
    }
}
