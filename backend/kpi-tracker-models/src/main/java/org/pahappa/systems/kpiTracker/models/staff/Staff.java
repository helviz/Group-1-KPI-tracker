package org.pahappa.systems.kpiTracker.models.staff;

import org.pahappa.systems.kpiTracker.models.department.Department;
import org.sers.webutils.model.BaseEntity;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.security.User;

import javax.persistence.*;

@Entity
@Table(name = "staff")
public class Staff extends BaseEntity {

    private String firstName;
    private String lastName;
    private String email;
    private User user; // Can be null if staff has no user account yet
    private boolean active = true;
    private Department department;
    private String status = "ACTIVE";

    @Column(name = "first_name", nullable = false)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "last_name", nullable = false)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(name = "email", nullable = false, unique = true)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * A staff member may not have a user account. The user account is for system access.
     * EAGER fetch is suitable here to easily check if a user account exists in the UI.
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", unique = true) // nullable is true by default
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "is_active", nullable = false)
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Column(name = "status", nullable = false)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return this.firstName + " " + this.lastName;
    }

    @Transient
    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }
}