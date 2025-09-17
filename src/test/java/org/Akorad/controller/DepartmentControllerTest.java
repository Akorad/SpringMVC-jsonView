package org.Akorad.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.Akorad.entity.Department;
import org.Akorad.repository.DepartmentRepository;
import org.Akorad.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

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
    void testCreateDepartment_Success() throws Exception {
        Department newDept = new Department();
        newDept.setName("HR");

        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDept)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("HR"));

        assertThat(departmentRepository.findAll()).hasSize(2);
    }

    @Test
    void testGetDepartmentById_Success() throws Exception {
        mockMvc.perform(get("/api/departments/" + department.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(department.getId()))
                .andExpect(jsonPath("$.name").value("IT"));
    }

    @Test
    void testGetDepartmentById_NotFound() throws Exception {
        mockMvc.perform(get("/api/departments/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateDepartment_Success() throws Exception {
        Department updateDept = new Department();
        updateDept.setName("Finance");

        mockMvc.perform(put("/api/departments/" + department.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDept)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Finance"));

        assertThat(departmentRepository.findById(department.getId()).get().getName())
                .isEqualTo("Finance");
    }

    @Test
    void testDeleteDepartment_Success() throws Exception {
        mockMvc.perform(delete("/api/departments/" + department.getId()))
                .andExpect(status().isNoContent());

        assertThat(departmentRepository.findById(department.getId())).isEmpty();
    }
}
