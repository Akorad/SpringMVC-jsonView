package org.Akorad.service;

import org.Akorad.entity.Department;
import org.Akorad.entity.Employee;
import org.Akorad.exception.ResourceNotFoundException;
import org.Akorad.projection.EmployeeProjection;
import org.Akorad.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmployeeServiceImplTest {
    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private Department department;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        department = new Department();
        department.setId(1L);
        department.setName("IT");

        employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setPosition("Developer");
        employee.setDepartment(department);
    }

    @Test
    void createEmployee_ShouldReturnSavedEmployee() {
        when(employeeRepository.save(employee)).thenReturn(employee);

        Employee result = employeeService.createEmployee(employee);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    void updateEmployee_WhenEmployeeExists_ShouldReturnUpdatedEmployee() {
        Employee updatedDetails = new Employee();
        updatedDetails.setFirstName("Jane");
        updatedDetails.setLastName("Smith");
        updatedDetails.setPosition("Manager");
        updatedDetails.setDepartment(department);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(i -> i.getArgument(0));

        Employee result = employeeService.updateEmployee(1L, updatedDetails);

        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals("Manager", result.getPosition());
        verify(employeeRepository).save(employee);
    }

    @Test
    void updateEmployee_WhenEmployeeNotFound_ShouldThrowException() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> employeeService.updateEmployee(1L, employee));

        assertEquals("Employee not found with id 1", ex.getMessage());
    }

    @Test
    void deleteEmployee_WhenEmployeeExists_ShouldCallDelete() {
        when(employeeRepository.existsById(1L)).thenReturn(true);

        employeeService.deleteEmployee(1L);

        verify(employeeRepository).deleteById(1L);
    }

    @Test
    void deleteEmployee_WhenEmployeeNotFound_ShouldThrowException() {
        when(employeeRepository.existsById(1L)).thenReturn(false);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> employeeService.deleteEmployee(1L));

        assertEquals("Employee not found with id 1", ex.getMessage());
    }

    @Test
    void getAllEmployeeProjections_ShouldReturnList() {
        EmployeeProjection projection = mock(EmployeeProjection.class);
        when(employeeRepository.findAllProjection()).thenReturn(List.of(projection));

        List<EmployeeProjection> result = employeeService.getAllEmployeeProjections();

        assertEquals(1, result.size());
        verify(employeeRepository).findAllProjection();
    }

    @Test
    void getEmployeeProjectionById_WhenExists_ShouldReturnProjection() {
        EmployeeProjection projection = mock(EmployeeProjection.class);
        when(employeeRepository.findProjectedById(1L)).thenReturn(Optional.of(projection));

        EmployeeProjection result = employeeService.getEmployeeProjectionById(1L);

        assertNotNull(result);
        verify(employeeRepository).findProjectedById(1L);
    }

    @Test
    void getEmployeeProjectionById_WhenNotFound_ShouldThrowException() {
        when(employeeRepository.findProjectedById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> employeeService.getEmployeeProjectionById(1L));

        assertEquals("Employee not found with id 1", ex.getMessage());
    }
}
