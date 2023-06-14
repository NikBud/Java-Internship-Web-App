package com.endava.developement.java.webapphomework.services.security;

import com.endava.developement.java.webapphomework.exceptions.EmployeeNotFoundException;
import com.endava.developement.java.webapphomework.models.Employee;
import com.endava.developement.java.webapphomework.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public UserDetailsServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByEmail(username).orElseThrow(EmployeeNotFoundException::new);

        UserDetails userDetails = User.builder()
                .username(employee.getEmail())
                .password(employee.getPhoneNumber())
                .roles("USER")
                .build();

        return userDetails;
    }
}
