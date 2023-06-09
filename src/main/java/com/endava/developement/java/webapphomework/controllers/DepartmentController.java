package com.endava.developement.java.webapphomework.controllers;

import com.endava.developement.java.webapphomework.DTO.DepartmentRequest;
import com.endava.developement.java.webapphomework.DTO.DepartmentResponse;
import com.endava.developement.java.webapphomework.services.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @Autowired
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public List<DepartmentResponse> getAll() {
        return departmentService.findAll();
    }

    @GetMapping("/{id}")
    public DepartmentResponse getById(@PathVariable("id") Long id) {
        return departmentService.findById(id);
    }

    @PostMapping
    public ResponseEntity<DepartmentResponse> add(@RequestBody DepartmentRequest departmentRequest){
        DepartmentResponse response = departmentService.saveDepartment(departmentRequest);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponse> edit(@PathVariable("id") Long id, @RequestBody DepartmentRequest departmentRequest){
        DepartmentResponse response = departmentService.editDepartment(id, departmentRequest);

        return ResponseEntity.ok(response);
    }
}
