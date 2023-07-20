package com.endava.developement.java.webapphomework.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EmployeeRequest {

    @Size(min = 3, max = 50)
    @NotBlank
    private String firstName;


    @Size(min = 3, max = 50)
    @NotBlank
    private String lastName;


    @Email
    @Size(min = 3, max = 50)
    @NotBlank
    private String email;


    @Size(min = 9, max = 20)
    @NotBlank
    private String phoneNumber;

    @NotNull
    private Float salary;

    @Size(min = 2, max = 30)
    @NotBlank
    private String departmentName;

    @Size(min = 8, max = 16)
    @NotBlank
    private String password;

    @Size(min = 8, max = 16)
    @NotBlank
    private String repeatPassword;


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Float getSalary() {
        return salary;
    }

    public void setSalary(Float salary) {
        this.salary = salary;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }
}
