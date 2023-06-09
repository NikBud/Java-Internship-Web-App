package com.endava.developement.java.webapphomework.DTO;

public class DepartmentResponse {

    private Long id;

    private String name;

    private String location;

    private Integer countOfEmployees;

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

    public Integer getCountOfEmployees() {
        return countOfEmployees;
    }

    public void setCountOfEmployees(Integer countOfEmployees) {
        this.countOfEmployees = countOfEmployees;
    }
}
