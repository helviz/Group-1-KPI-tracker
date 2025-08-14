package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.models.department.Department;

import java.util.List;

public interface DepartmentService extends GenericService<Department> {
    //get all departments in a company/organization
    public List<Department> getAllDepartments();
    //get Department by ID;
    public Department getDepartmentById(String id);
}
