package org.pahappa.systems.kpiTracker.models.team;

import org.pahappa.systems.kpiTracker.models.department.Department;
import org.sers.webutils.model.BaseEntity;
import org.sers.webutils.model.security.User;

import javax.persistence.*;

@Entity
@Table(name = "teams")
public class Team extends BaseEntity {

    private static final long serialVersionUID = 1L;
    private String teamName;
    private User teamLead;
    private String description;
    private Department department;

    @Column(name = "team_name", nullable = false, unique = true)
    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_lead_id", nullable = false)
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return this.teamName;
    }

    @Override
    public int hashCode() {
        return super.getId() != null ? this.getClass().hashCode() + super.getId().hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Team && (super.getId() != null) ? super.getId().equals(((Team) object).getId())
                : (object == this);
    }
}