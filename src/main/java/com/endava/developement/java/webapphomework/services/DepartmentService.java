package com.endava.developement.java.webapphomework.services;

import com.endava.developement.java.webapphomework.DTO.DepartmentRequest;
import com.endava.developement.java.webapphomework.DTO.DepartmentResponse;
import com.endava.developement.java.webapphomework.models.Department;
import com.endava.developement.java.webapphomework.repositories.DepartmentDAO;
import com.endava.developement.java.webapphomework.util.DepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DepartmentService {

    private final DepartmentDAO departmentDAO;

    private final DepartmentMapper departmentMapper;

    @Autowired
    public DepartmentService(DepartmentDAO departmentRepository, DepartmentMapper departmentMapper) {
        this.departmentDAO = departmentRepository;
        this.departmentMapper = departmentMapper;
    }


    public List<DepartmentResponse> findAll() {
        return departmentDAO.findAll()
                .stream()
                .map(departmentMapper::mapToDepartmentResponse)
                .collect(Collectors.toList());
    }

    public DepartmentResponse findById(Long id) {
        return departmentMapper
                .mapToDepartmentResponse
                        (departmentDAO.findById(id));
    }

    @Transactional
    public DepartmentResponse saveDepartment(DepartmentRequest departmentRequest) {
        Department department = departmentMapper.mapRequestDTOtoEntity(departmentRequest);

        return departmentMapper.mapToDepartmentResponse(departmentDAO.save(department));
    }

    @Transactional
    public DepartmentResponse editDepartment(Long id, DepartmentRequest departmentRequest) {
        Department existingDepartment = departmentDAO
                .findById(id);

        departmentMapper.mapRequestDTOAndEntity(existingDepartment, departmentRequest);

        return departmentMapper.mapToDepartmentResponse
                (departmentDAO.edit(existingDepartment));
    }
}
