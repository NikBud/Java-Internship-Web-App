package com.endava.developement.java.webapphomework.config;

import com.endava.developement.java.webapphomework.OAuth.CustomOAuth2UserService;
import com.endava.developement.java.webapphomework.models.Employee;
import com.endava.developement.java.webapphomework.security.AuthProviderImpl;
import com.endava.developement.java.webapphomework.services.EmployeeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Optional;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthProviderImpl authProvider;
    private final CustomOAuth2UserService oAuth2UserService;
    private final EmployeeService employeeService;
    private final HttpSession httpSession;

    @Autowired
    public SecurityConfig(AuthProviderImpl authProvider, CustomOAuth2UserService oAuth2UserService, EmployeeService employeeService, HttpSession httpSession) {
        this.authProvider = authProvider;
        this.oAuth2UserService = oAuth2UserService;
        this.employeeService = employeeService;
        this.httpSession = httpSession;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authProvider)
                .authorizeHttpRequests((authorize) ->
                        authorize
                                .requestMatchers("/authentication/login",  "/authentication/register", "/error", "/auth/**", "/oauth2/**")
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                )
                .oauth2Login((configurer) ->
                        configurer
                                .loginPage("/authentication/login")
                                .userInfoEndpoint(customizer ->
                                        customizer.userService(oAuth2UserService))
                                .defaultSuccessUrl("/employees", true)
                                .failureUrl("/authentication/login?error")
                                .successHandler(new AuthenticationSuccessHandler() {
                                    @Override
                                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                                        DefaultOidcUser oauthUser = (DefaultOidcUser) authentication.getPrincipal();

                                        Optional<Employee> employee = employeeService.getByEmail(oauthUser.getAttribute("email"));
                                        if (employee.isPresent()) {
                                            response.sendRedirect("/employees");
                                        }
                                        else {
                                            httpSession.invalidate();
                                            response.sendRedirect("/authentication/login?error=googleNoSuchUser");
                                        }
                                    }
                                })
                )
                .formLogin((configurer) ->
                        configurer
                                .loginPage("/authentication/login")
                                .loginProcessingUrl("/process_login")
                                .defaultSuccessUrl("/employees",true)
                                .failureUrl("/authentication/login?error")
                );
                //.httpBasic(Customizer.withDefaults());

        return http.build();
    }
}