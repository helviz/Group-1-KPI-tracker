package org.pahappa.systems.kpiTracker.models.department;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.sers.webutils.model.BaseEntity;
import org.sers.webutils.model.security.User;


import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
@Table(name="CompanyDepartments" )
public class Department extends BaseEntity {
    //add the team numbers associated to this department
    private static final long serialVersionUID = 6095671201979163425L;
    @NotBlank(message = "Department name is required") // not null & not empty
    @Size(min = 3, max = 100, message = "Department name must be between 3 and 100 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9 .,!?'-]+$",
            message = "Department name can only contain letters, numbers, spaces, and basic punctuation"
    )
    @Column(length = 100, nullable = false, unique = true) // DB constraints
    private String name;

    @ManyToOne
    @JoinColumn(name = "departmentLead")
    private User departmentLead;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;


}
