package org.Akorad.service.impl;

import lombok.RequiredArgsConstructor;
import org.Akorad.entity.Employee;
import org.Akorad.exception.ResourceNotFoundException;
import org.Akorad.projection.EmployeeProjection;
import org.Akorad.repository.EmployeeRepository;
import org.Akorad.service.EmployeeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    @Override
    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public Employee updateEmployee(Long id, Employee employeeDetails) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + id));

        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());
        employee.setPosition(employeeDetails.getPosition());
        employee.setDepartment(employeeDetails.getDepartment());

        return employeeRepository.save(employee);
    }

    @Override
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee not found with id " + id);
        }
        employeeRepository.deleteById(id);
    }

    @Override
    public List<EmployeeProjection> getAllEmployeeProjections() {
        return employeeRepository.findAllProjection();
    }

    @Override
    public EmployeeProjection getEmployeeProjectionById(Long id) {
        return employeeRepository.findProjectedById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + id));
    }
}
