package org.Akorad.projection;

public interface EmployeeProjection {

    String getFirstName();

    String getLastName();

    String getPosition();

    String getDepartmentName();

    default String getFullName() {
        return getFirstName() + " " + getLastName();
    }
}
