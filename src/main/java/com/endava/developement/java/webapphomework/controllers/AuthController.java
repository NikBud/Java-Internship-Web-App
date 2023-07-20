package com.endava.developement.java.webapphomework.controllers;

import com.endava.developement.java.webapphomework.DTO.EmployeeRequest;
import com.endava.developement.java.webapphomework.DTO.EmployeeResponse;
import com.endava.developement.java.webapphomework.models.Employee;
import com.endava.developement.java.webapphomework.services.EmployeeService;
import com.endava.developement.java.webapphomework.util.EmployeeMapper;
import com.endava.developement.java.webapphomework.util.EmployeeValidator;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/authentication")
public class AuthController {

    private final EmployeeService employeeService;
    private final EmployeeValidator employeeValidator;
    private final EmployeeMapper employeeMapper;
    private final HttpSession httpSession;

    @Autowired
    public AuthController(EmployeeService employeeService, EmployeeValidator employeeValidator, EmployeeMapper employeeMapper, HttpSession httpSession) {
        this.employeeService = employeeService;
        this.employeeValidator = employeeValidator;
        this.employeeMapper = employeeMapper;
        this.httpSession = httpSession;
    }

    @GetMapping("userInfo")
    @ResponseBody
    public EmployeeResponse showUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        Optional<Employee> employee;
        if (principal instanceof UserDetails userDetails) {
            employee = employeeService.getByEmail(userDetails.getUsername());
        }
        else {
            DefaultOidcUser oauthUser = (DefaultOidcUser) principal;
            employee = employeeService.getByEmail(oauthUser.getAttribute("email"));
        }
        return employeeMapper.convertEntityToDTOResponse(employee.orElse(null));
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/logout")
    public String logout(){
        httpSession.invalidate();
        return "auth/login";
    }

        @GetMapping("/register")
    public String registerPage(@ModelAttribute EmployeeRequest employeeRequest){
        return "auth/registration";
    }

    @PostMapping("/register")
    public String performRegistration(@ModelAttribute @Valid EmployeeRequest employeeRequest,
                                      BindingResult bindingResult){

        employeeValidator.validate(employeeRequest, bindingResult);

        if (bindingResult.hasErrors())
            return "/auth/registration";

        employeeService.saveEmployee(employeeRequest);

        return "redirect:/auth/login";
    }

}
