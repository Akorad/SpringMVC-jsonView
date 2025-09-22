package org.Akorad.service;

import org.Akorad.entity.Department;
import org.Akorad.exception.ResourceNotFoundException;
import org.Akorad.repository.DepartmentRepository;
import org.Akorad.service.impl.DepartmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DepartmentServiceImplTest {
    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private Department department;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        department = new Department();
        department.setId(1L);
        department.setName("IT");
    }

    @Test
    void createDepartment_ShouldReturnSavedDepartment() {
        when(departmentRepository.save(department)).thenReturn(department);

        Department result = departmentService.createDepartment(department);

        assertNotNull(result);
        assertEquals("IT", result.getName());
        verify(departmentRepository, times(1)).save(department);
    }

    @Test
    void updateDepartment_WhenDepartmentExists_ShouldReturnUpdatedDepartment() {
        Department updatedDetails = new Department();
        updatedDetails.setName("HR");

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentRepository.save(any(Department.class))).thenAnswer(i -> i.getArgument(0));

        Department result = departmentService.updateDepartment(1L, updatedDetails);

        assertEquals("HR", result.getName());
        verify(departmentRepository).save(department);
    }

    @Test
    void updateDepartment_WhenDepartmentNotFound_ShouldThrowException() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> departmentService.updateDepartment(1L, department));

        assertEquals("Department not found with id 1", ex.getMessage());
    }

    @Test
    void deleteDepartment_WhenDepartmentExists_ShouldCallDelete() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        departmentService.deleteDepartment(1L);

        verify(departmentRepository).deleteById(1L);
    }

    @Test
    void deleteDepartment_WhenDepartmentNotFound_ShouldThrowException() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> departmentService.deleteDepartment(1L));

        assertEquals("Department not found with id 1", ex.getMessage());
    }

    @Test
    void getDepartmentById_WhenDepartmentExists_ShouldReturnDepartment() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        Department result = departmentService.getDepartmentById(1L);

        assertNotNull(result);
        assertEquals("IT", result.getName());
    }

    @Test
    void getDepartmentById_WhenNotFound_ShouldThrowException() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> departmentService.getDepartmentById(1L));

        assertEquals("Department not found with id 1", ex.getMessage());
    }
}
