package org.Akorad.repository;

import org.Akorad.entity.Employee;
import org.Akorad.projection.EmployeeProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e.firstName AS firstName, e.lastName AS lastName, e.position AS position, d.name AS departmentName " +
            "FROM Employee e JOIN e.department d")
    List<EmployeeProjection> findAllProjection();

    @Query("SELECT e.firstName AS firstName, e.lastName AS lastName, e.position AS position, d.name AS departmentName " +
            "FROM Employee e JOIN e.department d WHERE e.id = :id")
    Optional<EmployeeProjection> findProjectedById(@Param("id") Long id);
}
