package com.endava.developement.java.webapphomework.util;

import com.endava.developement.java.webapphomework.DTO.DepartmentRequest;
import com.endava.developement.java.webapphomework.DTO.DepartmentResponse;
import com.endava.developement.java.webapphomework.models.Department;
import com.endava.developement.java.webapphomework.repositories.EmployeeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    private final ModelMapper modelMapper;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public DepartmentMapper(ModelMapper modelMapper, EmployeeRepository employeeRepository) {
        this.modelMapper = modelMapper;
        this.employeeRepository = employeeRepository;
    }

    public DepartmentResponse mapToDepartmentResponse(Department department) {
        DepartmentResponse response = modelMapper.map(department, DepartmentResponse.class);

        response.setCountOfEmployees
                (employeeRepository.findByDepartment
                        (department.getName()).size());

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
