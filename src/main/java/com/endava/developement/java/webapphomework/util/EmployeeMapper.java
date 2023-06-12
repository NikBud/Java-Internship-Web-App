package com.endava.developement.java.webapphomework.util;

import com.endava.developement.java.webapphomework.DTO.EmployeeRequest;
import com.endava.developement.java.webapphomework.DTO.EmployeeResponse;
import com.endava.developement.java.webapphomework.exceptions.DepartmentNotFoundException;
import com.endava.developement.java.webapphomework.models.Department;
import com.endava.developement.java.webapphomework.models.Employee;
import com.endava.developement.java.webapphomework.repositories.DepartmentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class EmployeeMapper {

    private final ModelMapper modelMapper;
    private final DepartmentRepository departmentRepository;

    @Autowired
    public EmployeeMapper(ModelMapper modelMapper, DepartmentRepository departmentRepository) {
        this.modelMapper = modelMapper;
        this.departmentRepository = departmentRepository;
        modelMapperSetupEntityToDTOResponse();
    }

    public EmployeeResponse convertEntityToDTOResponse(Employee employee){
        return modelMapper.map(employee, EmployeeResponse.class);
    }

    @Transactional(readOnly = true)
    public Employee convertDTORequestToEntity(EmployeeRequest employeeRequest){
        Department department = departmentRepository.findByName(employeeRequest.getDepartmentName().trim().replaceAll("\\s", " "))
                .orElseThrow(DepartmentNotFoundException::new);

        Employee employee = new Employee();
        employee.setFirstName(employeeRequest.getFirstName());
        employee.setLastName(employeeRequest.getLastName());
        employee.setEmail(employeeRequest.getEmail());
        employee.setSalary(employeeRequest.getSalary());
        employee.setPhoneNumber(employeeRequest.getPhoneNumber());
        employee.setDepartment(department);

        return employee;
    }

    @Transactional(readOnly = true)
    public void mapRequestDTOAndEntity(Employee employeeToChange, EmployeeRequest changedEmployee){
        Department changedDepartment = departmentRepository
                .findByName(changedEmployee.getDepartmentName().trim().replaceAll("\\s", " "))
                    .orElseThrow(DepartmentNotFoundException::new);

        employeeToChange.setFirstName(changedEmployee.getFirstName());
        employeeToChange.setLastName(changedEmployee.getLastName());
        employeeToChange.setEmail(changedEmployee.getEmail());
        employeeToChange.setSalary(changedEmployee.getSalary());
        employeeToChange.setPhoneNumber(changedEmployee.getPhoneNumber());
        employeeToChange.setDepartment(changedDepartment);
    }


    private void modelMapperSetupEntityToDTOResponse(){
        modelMapper.createTypeMap(Employee.class, EmployeeResponse.class)
                .addMappings(mapper -> mapper
                        .map(src -> src.getDepartment().getName(), (dst, value) -> dst.setDepartmentName((String) value)));
    }


}
