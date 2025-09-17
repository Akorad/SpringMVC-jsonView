package org.Akorad.service;

import org.Akorad.entity.Department;

public interface DepartmentService {
    Department createDepartment(Department department);
    Department updateDepartment(Long id, Department departmentDetails);
    void deleteDepartment(Long id);
    Department getDepartmentById(Long id);
}
