package org.pahappa.systems.kpiTracker.models.department;

import lombok.Setter;
import org.sers.webutils.model.BaseEntity;
import org.sers.webutils.model.security.User;


import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Objects;


@Setter
@Entity
@Table(name="company_departments" )
public class Department extends BaseEntity {
    private static final long serialVersionUID = 6095671201979163425L;
    private String name;
    private User departmentLead;
    private String description;

    @Column(length = 100, nullable = false, unique = true) // DB constraints
    public String getName() {
        return name;
    }

    @ManyToOne
    @JoinColumn(name = "department_lead " )
    public User getDepartmentLead() {
        return departmentLead;
    }

    @Size(max = 255, message = "Description must not exceed 255 characters")
    @Column(name = "descriptions")
    public String getDescription() {
        return description;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Department that = (Department) o;

        // If both entities are persisted, compare IDs
        if (!this.isNew() && !that.isNew()) {
            return getId().equals(that.getId());
        }
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        if (!isNew()) {
            return getId().hashCode();
        }
        return name != null ? name.hashCode() : 0;
    }

}
