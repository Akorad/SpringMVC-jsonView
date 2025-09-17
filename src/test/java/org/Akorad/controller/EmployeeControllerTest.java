package org.Akorad.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.Akorad.entity.Department;
import org.Akorad.entity.Employee;
import org.Akorad.repository.DepartmentRepository;
import org.Akorad.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Department department;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        departmentRepository.deleteAll();

        department = new Department();
        department.setName("IT");
        department = departmentRepository.save(department);
    }

    @Test
    void testCreateEmployee_Success() throws Exception {
        Employee employee = new Employee();
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setPosition("Developer");
        employee.setSalary(new BigDecimal(60000));
        employee.setDepartment(department);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.position").value("Developer"));

        assertThat(employeeRepository.findAll()).hasSize(1);
    }

    @Test
    void testGetEmployeeById_Success() throws Exception {
        Employee employee = new Employee();
        employee.setFirstName("Jane");
        employee.setLastName("Smith");
        employee.setPosition("Manager");
        employee.setSalary(new BigDecimal(60000));
        employee.setDepartment(department);
        employee = employeeRepository.save(employee);

        mockMvc.perform(get("/api/employees/" + employee.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Jane Smith"))
                .andExpect(jsonPath("$.position").value("Manager"))
                .andExpect(jsonPath("$.departmentName").value("IT"));
    }

    @Test
    void testGetEmployeeById_NotFound() throws Exception {
        mockMvc.perform(get("/api/employees/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllEmployeeProjections_ReturnsList() throws Exception {
        Employee e1 = new Employee();
        e1.setFirstName("John");
        e1.setLastName("Doe");
        e1.setPosition("Developer");
        e1.setSalary(new BigDecimal(60000));

        e1.setDepartment(department);
        employeeRepository.save(e1);

        Employee e2 = new Employee();
        e2.setFirstName("Jane");
        e2.setLastName("Smith");
        e2.setPosition("Manager");
        e2.setSalary(new BigDecimal(30000));

        e2.setDepartment(department);
        employeeRepository.save(e2);

        mockMvc.perform(get("/api/employees/projections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testUpdateEmployee_Success() throws Exception {
        Employee employee = new Employee();
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setSalary(new BigDecimal(60000));
        employee.setPosition("Developer");
        employee.setDepartment(department);
        employee = employeeRepository.save(employee);

        Employee updateRequest = new Employee();
        updateRequest.setFirstName("John");
        updateRequest.setLastName("Doe");
        updateRequest.setPosition("Senior Developer");
        employee.setSalary(new BigDecimal(60000));
        updateRequest.setDepartment(department);

        mockMvc.perform(put("/api/employees/" + employee.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.position").value("Senior Developer"));

        assertThat(employeeRepository.findById(employee.getId()).get().getPosition())
                .isEqualTo("Senior Developer");
    }

    @Test
    void testDeleteEmployee_Success() throws Exception {
        Employee employee = new Employee();
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setPosition("Developer");
        employee.setSalary(new BigDecimal(60000));
        employee.setDepartment(department);
        employee = employeeRepository.save(employee);

        mockMvc.perform(delete("/api/employees/" + employee.getId()))
                .andExpect(status().isNoContent());

        assertThat(employeeRepository.findById(employee.getId())).isEmpty();
    }
}