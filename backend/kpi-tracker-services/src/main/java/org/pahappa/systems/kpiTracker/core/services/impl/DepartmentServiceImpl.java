package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.dao.DepartmentDao;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.StaffService;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("departmentService") // It's good practice to name the Spring bean
@Transactional
public class DepartmentServiceImpl extends GenericServiceImpl<Department> implements DepartmentService {
    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private StaffService staffService;


    @Override
    public Department saveInstance(Department entityInstance) throws ValidationFailedException, OperationFailedException {
        // 'super.save()' is inherited from BaseDAOImpl via GenericServiceImpl
        return super.save(entityInstance);
    }

    /**
     * A department is deletable only if it has no staff members assigned to it.
     */
    @Override
    public boolean isDeletable(Department instance) throws OperationFailedException {
        // A department can be deleted only if it has no staff members.
        return staffService.getStaffByDepartment(instance).isEmpty();
    }

    @Override
    public List<Department> getAllDepartments() {
        return super.getAllInstances();
    }

    @Override
    public Department getDepartmentById(String id) {
        return this.departmentDao.find(id);
    }


}
