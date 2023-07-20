package com.endava.developement.java.webapphomework.util;

import com.endava.developement.java.webapphomework.DTO.EmployeeRequest;
import com.endava.developement.java.webapphomework.models.Employee;
import com.endava.developement.java.webapphomework.services.DepartmentService;
import com.endava.developement.java.webapphomework.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
public class EmployeeValidator implements Validator {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    @Autowired
    public EmployeeValidator(EmployeeService employeeService, DepartmentService departmentService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return EmployeeRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EmployeeRequest request = (EmployeeRequest) target;

        if (!request.getPassword().equals(request.getRepeatPassword()))
            errors.rejectValue("repeatPassword", "", "Password and repeat password fields do not match !");

        Optional<Employee> emailCheck = employeeService.getByEmail(request.getEmail());
        if (emailCheck.isPresent())
            errors.rejectValue("email", "", "This email is already registered in the system !");

        Optional<Employee> phoneCheck = employeeService.getByPhoneNumber(request.getPhoneNumber());
        if (phoneCheck.isPresent())
            errors.rejectValue("phoneNumber", "", "This phone number is already registered in the system !");

        if (departmentService.findByName(request.getDepartmentName()).isEmpty())
            errors.rejectValue("departmentName", "", "There is no such department in the system !");
    }
}
