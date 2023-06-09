package com.endava.developement.java.webapphomework.repositories;

import com.endava.developement.java.webapphomework.models.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByName(String name);
}
