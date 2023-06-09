package com.endava.developement.java.webapphomework.services;

import com.endava.developement.java.webapphomework.DTO.EmployeeRequest;
import com.endava.developement.java.webapphomework.DTO.EmployeeResponse;
import com.endava.developement.java.webapphomework.exceptions.EmployeeNotFoundException;
import com.endava.developement.java.webapphomework.models.Employee;
import com.endava.developement.java.webapphomework.repositories.EmployeeDAO;
import com.endava.developement.java.webapphomework.util.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeDAO employeeDAO;
    private final EmployeeMapper employeeMapper;

    @Autowired
    public EmployeeService(EmployeeDAO employeeDAO, EmployeeMapper employeeMapper) {
        this.employeeDAO = employeeDAO;
        this.employeeMapper = employeeMapper;
    }

    public List<EmployeeResponse> getAll(){
        try {
            return employeeDAO.getAll()
                    .stream()
                    .map(employeeMapper::convertEntityToDTOResponse)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public EmployeeResponse getOne(Long id){
        Employee foundEmployee = employeeDAO.findById(id);
        if (foundEmployee.getId() == null) throw new EmployeeNotFoundException();

        return employeeMapper.convertEntityToDTOResponse(foundEmployee);
    }

    @Transactional
    public EmployeeResponse saveEmployee(EmployeeRequest employeeRequest){
        Employee employee = employeeMapper.convertDTORequestToEntity(employeeRequest);

        return employeeMapper.convertEntityToDTOResponse(employeeDAO.save(employee));
    }

    @Transactional
    public EmployeeResponse editEmployee(Long id, EmployeeRequest employeeRequest) {
        Employee existingEmployee = employeeDAO.findById(id);

        employeeMapper.mapRequestDTOAndEntity(existingEmployee, employeeRequest);

        return employeeMapper.convertEntityToDTOResponse
                (employeeDAO.saveAfterEdit(existingEmployee));
    }
}
