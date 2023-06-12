package com.endava.developement.java.webapphomework.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name")
    @NotNull
    @NotBlank
    @NotEmpty
    @Size(min = 3, max = 50)
    private String firstName;

    @Column(name = "last_name")
    @NotNull
    @NotBlank
    @NotEmpty
    @Size(min = 3, max = 50)
    private String lastName;

    @Column(name = "email")
    @Email
    @NotNull
    @Size(min = 3, max = 20)
    private String email;

    @Column(name = "phone_number")
    @NotNull
    @NotBlank
    @NotEmpty
    @Size(min = 9, max = 15)
    private String phoneNumber;

    @Column(name = "salary")
    @NotNull
    private Float salary;

    @ManyToOne
    @JoinColumn(name = "department_id")
    @NotNull
    private Department department;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
