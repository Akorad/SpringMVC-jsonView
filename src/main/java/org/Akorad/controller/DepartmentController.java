package org.Akorad.controller;

import lombok.RequiredArgsConstructor;
import org.Akorad.entity.Department;
import org.Akorad.service.DepartmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('MODERATOR', 'SUPER_ADMIN')")
    public ResponseEntity<Department> createDepartment(@RequestBody Department department) {
        Department createdDepartment = departmentService.createDepartment(department);
        return ResponseEntity.ok(createdDepartment);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'SUPER_ADMIN')")
    public ResponseEntity<Department> getDepartmentById(@PathVariable("id") Long id) {
        Department department = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(department);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MODERATOR', 'SUPER_ADMIN')")
    public ResponseEntity<Department> updateDepartment(@PathVariable("id") Long id, @RequestBody Department departmentDetails) {
        Department updatedDepartment = departmentService.updateDepartment(id, departmentDetails);
        return ResponseEntity.ok(updatedDepartment);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteDepartment(@PathVariable("id") Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
}
