package com.endava.developement.java.webapphomework.services;

import com.endava.developement.java.webapphomework.DTO.EmployeeRequest;
import com.endava.developement.java.webapphomework.DTO.EmployeeResponse;
import com.endava.developement.java.webapphomework.exceptions.EmployeeNotFoundException;
import com.endava.developement.java.webapphomework.models.Employee;
import com.endava.developement.java.webapphomework.repositories.EmployeeRepository;
import com.endava.developement.java.webapphomework.util.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Employee> getAll(){
        return employeeRepository.findAll();
    }

    public Optional<Employee> getOne(Long id){
        return employeeRepository.findById(id);
    }

    public Optional<Employee> getByEmail(String email){
        return employeeRepository.findByEmail(email);
    }

    public Optional<Employee> getByPhoneNumber(String phoneNumber) {
        return employeeRepository.findByPhoneNumber(phoneNumber);
    }

    @Transactional
    public EmployeeResponse saveEmployee(EmployeeRequest employeeRequest){
        employeeRequest
                .setPassword
                        (passwordEncoder.encode
                                (employeeRequest.getPassword()));


        Employee employee = employeeMapper.convertDTORequestToEntity(employeeRequest);

        return employeeMapper.convertEntityToDTOResponse(employeeRepository.save(employee));
    }

    @Transactional
    public EmployeeResponse editEmployee(Long id, EmployeeRequest employeeRequest) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(EmployeeNotFoundException::new);


        employeeMapper.mapRequestDTOAndEntity(existingEmployee, employeeRequest);

        return employeeMapper.convertEntityToDTOResponse
                (employeeRepository.save(existingEmployee));
    }


}
