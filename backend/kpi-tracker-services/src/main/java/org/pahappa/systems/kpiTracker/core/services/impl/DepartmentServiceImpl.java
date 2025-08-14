package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.dao.DepartmentDao;
import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
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


    @Override
    public Department saveInstance(Department entityInstance) throws ValidationFailedException, OperationFailedException {
        // 'super.save()' is inherited from BaseDAOImpl via GenericServiceImpl
        return super.save(entityInstance);
    }

    /**
     * This method specifies if a department can be deleted. For now, we'll say yes.
     * This is an abstract method from your GenericServiceImpl that must be implemented.
     */
    @Override
    public boolean isDeletable(Department instance) throws OperationFailedException {
        return true;
    }

    @Override
    public List<Department> getAllDepartments() {
        return super.getAllInstances();
    }

    @Override
    public Department getDepartmentById(String id) {
        return this.departmentDao.searchUniqueByPropertyEqual("id", id);
    }


}
