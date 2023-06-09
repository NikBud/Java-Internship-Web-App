package com.endava.developement.java.webapphomework.controllers;

import com.endava.developement.java.webapphomework.DTO.EmployeeRequest;
import com.endava.developement.java.webapphomework.DTO.EmployeeResponse;
import com.endava.developement.java.webapphomework.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<EmployeeResponse> findAll(){
        return employeeService.getAll();
    }

    @GetMapping("/{id}")
    public EmployeeResponse findOne(@PathVariable Long id){
        return employeeService.getOne(id);
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> save(@RequestBody EmployeeRequest employeeRequest){
        EmployeeResponse savedEmployee = employeeService.saveEmployee(employeeRequest);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedEmployee.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedEmployee);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> edit(@PathVariable("id") Long id, @RequestBody EmployeeRequest changedEmployee){
        EmployeeResponse response = employeeService.editEmployee(id, changedEmployee);

        return ResponseEntity.ok(response);
    }
}
