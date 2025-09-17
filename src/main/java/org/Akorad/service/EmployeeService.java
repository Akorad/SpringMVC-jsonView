package org.Akorad.service;

import org.Akorad.entity.Employee;
import org.Akorad.projection.EmployeeProjection;

import java.util.List;

public interface EmployeeService {
    Employee createEmployee(Employee employee);

    Employee updateEmployee(Long id, Employee employeeDetails);

    void deleteEmployee(Long id);

    List<EmployeeProjection> getAllEmployeeProjections();

    EmployeeProjection getEmployeeProjectionById(Long id);
}
