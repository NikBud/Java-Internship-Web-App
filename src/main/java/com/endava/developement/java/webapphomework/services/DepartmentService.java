package com.endava.developement.java.webapphomework.services;

import com.endava.developement.java.webapphomework.DTO.DepartmentRequest;
import com.endava.developement.java.webapphomework.DTO.DepartmentResponse;
import com.endava.developement.java.webapphomework.exceptions.DepartmentNotFoundException;
import com.endava.developement.java.webapphomework.models.Department;
import com.endava.developement.java.webapphomework.repositories.DepartmentRepository;
import com.endava.developement.java.webapphomework.util.DepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    private final DepartmentMapper departmentMapper;

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository, DepartmentMapper departmentMapper) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
    }


    public List<DepartmentResponse> findAll() {
        return departmentRepository.findAll()
                .stream()
                .map(departmentMapper::mapToDepartmentResponse)
                .collect(Collectors.toList());
    }

    public DepartmentResponse findById(Long id) {
        return departmentMapper
                .mapToDepartmentResponse
                        (departmentRepository
                                .findById(id).orElseThrow(DepartmentNotFoundException::new));
    }

    public Optional<Department> findByName(String name) {
        return departmentRepository.findByName(name);
    }

    @Transactional
    public DepartmentResponse saveDepartment(DepartmentRequest departmentRequest) {
        Department department = departmentMapper.mapRequestDTOtoEntity(departmentRequest);

        return departmentMapper.mapToDepartmentResponse(departmentRepository.save(department));
    }

    @Transactional
    public DepartmentResponse editDepartment(Long id, DepartmentRequest departmentRequest) {
        Department existingDepartment = departmentRepository
                .findById(id)
                .orElseThrow(DepartmentNotFoundException::new);

        departmentMapper.mapRequestDTOAndEntity(existingDepartment, departmentRequest);

        return departmentMapper.mapToDepartmentResponse
                (departmentRepository.save(existingDepartment));
    }
}
