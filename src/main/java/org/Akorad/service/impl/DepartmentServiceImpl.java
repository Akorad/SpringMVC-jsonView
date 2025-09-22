package org.Akorad.service.impl;

import lombok.RequiredArgsConstructor;
import org.Akorad.entity.Department;
import org.Akorad.exception.ResourceNotFoundException;
import org.Akorad.repository.DepartmentRepository;
import org.Akorad.service.DepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    public Department createDepartment(Department department) {
        return departmentRepository.save(department);
    }

    @Override
    public Department updateDepartment(Long id, Department departmentDetails) {
        Department department = getDepartmentById(id);
        department.setName(departmentDetails.getName());
        return departmentRepository.save(department);

    }

    @Override
    public void deleteDepartment(Long id) {
        getDepartmentById(id);
        departmentRepository.deleteById(id);
    }

    @Override
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Department not found with id " + id));
    }
}
