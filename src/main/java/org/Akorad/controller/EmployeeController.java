package org.Akorad.controller;

import lombok.RequiredArgsConstructor;
import org.Akorad.entity.Employee;
import org.Akorad.projection.EmployeeProjection;
import org.Akorad.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        Employee createdEmployee = employeeService.createEmployee(employee);
        return ResponseEntity.ok(createdEmployee);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeProjection> getEmployeeById(@PathVariable("id") Long id) {
        EmployeeProjection employee = employeeService.getEmployeeProjectionById(id);
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/projections")
    public ResponseEntity<java.util.List<EmployeeProjection>> getAllEmployeeProjections() {
        java.util.List<EmployeeProjection> employees = employeeService.getAllEmployeeProjections();
        return ResponseEntity.ok(employees);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable("id") Long id, @RequestBody Employee employeeDetails) {
        Employee updatedEmployee = employeeService.updateEmployee(id, employeeDetails);
        return ResponseEntity.ok(updatedEmployee);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable("id") Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
