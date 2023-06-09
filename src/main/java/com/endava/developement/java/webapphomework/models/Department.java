package com.endava.developement.java.webapphomework.models;

import java.util.List;

public class Department {


    private Long id;

    private String name;

    private String location;

    private List<Employee> employees;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
        employees.forEach(e -> e.setDepartment(this));
    }
}
