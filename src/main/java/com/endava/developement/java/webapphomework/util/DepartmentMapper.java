package com.endava.developement.java.webapphomework.util;

import com.endava.developement.java.webapphomework.DTO.DepartmentRequest;
import com.endava.developement.java.webapphomework.DTO.DepartmentResponse;
import com.endava.developement.java.webapphomework.models.Department;
import com.endava.developement.java.webapphomework.repositories.EmployeeDAO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    private final ModelMapper modelMapper;
    private final EmployeeDAO employeeDAO;

    @Autowired
    public DepartmentMapper(ModelMapper modelMapper, EmployeeDAO employeeDAO) {
        this.modelMapper = modelMapper;
        this.employeeDAO = employeeDAO;
    }

    public DepartmentResponse mapToDepartmentResponse(Department department) {
        DepartmentResponse response = modelMapper.map(department, DepartmentResponse.class);

        response.setCountOfEmployees
                (employeeDAO.findByDepartment(department.getId()).size());

        return response;
    }

    public Department mapRequestDTOtoEntity(DepartmentRequest departmentRequest) {
        return modelMapper.map(departmentRequest, Department.class);
    }

    public void mapRequestDTOAndEntity(Department departmentToChange, DepartmentRequest changedDepartment) {
        departmentToChange.setName(changedDepartment.getName());
        departmentToChange.setLocation(changedDepartment.getLocation());
    }
}
