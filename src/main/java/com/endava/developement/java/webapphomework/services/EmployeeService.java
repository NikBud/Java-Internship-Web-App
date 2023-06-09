package com.endava.developement.java.webapphomework.services;

import com.endava.developement.java.webapphomework.DTO.EmployeeRequest;
import com.endava.developement.java.webapphomework.DTO.EmployeeResponse;
import com.endava.developement.java.webapphomework.exceptions.EmployeeNotFoundException;
import com.endava.developement.java.webapphomework.models.Employee;
import com.endava.developement.java.webapphomework.repositories.EmployeeRepository;
import com.endava.developement.java.webapphomework.util.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;


    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    public List<EmployeeResponse> getAll(){
        return employeeRepository.findAll()
                .stream()
                .map(employeeMapper::convertEntityToDTOResponse)
                .collect(Collectors.toList());
    }

    public EmployeeResponse getOne(Long id){
        Employee foundEmployee = employeeRepository.findById(id).orElseThrow(EmployeeNotFoundException::new);

        return employeeMapper.convertEntityToDTOResponse(foundEmployee);
    }

    @Transactional
    public EmployeeResponse saveEmployee(EmployeeRequest employeeRequest){
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
