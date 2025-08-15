package org.pahappa.systems.kpiTracker.models.team;

import org.pahappa.systems.kpiTracker.models.department.Department;
import org.sers.webutils.model.BaseEntity;
import org.sers.webutils.model.security.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "teams")
public class Team extends BaseEntity {

    private static final long serialVersionUID = 1L;
    private String teamName;
    private User teamLead;
    private String description;
    private Department department;
    private Set<User> members = new HashSet<>();

    @Column(name = "team_name", nullable = false, unique = true)
    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }


    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "team_lead_id")
    public User getTeamLead() {
        return teamLead;
    }

    public void setTeamLead(User teamLead) {
        this.teamLead = teamLead;
    }

    @Column(name = "description", length = 500)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "department_id")
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "team_members",
            joinColumns = {@JoinColumn(name = "team_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    public Set<User> getMembers() {
        return members;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }


    @Override
    public String toString() {
        return this.teamName;
    }

    @Override
    public int hashCode() {
        // Persisted entities are identified by their ID
        return Objects.hash(super.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return super.getId() != null && Objects.equals(super.getId(), team.getId());
    }
}