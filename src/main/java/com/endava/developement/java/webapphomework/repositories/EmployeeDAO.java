package com.endava.developement.java.webapphomework.repositories;


import com.endava.developement.java.webapphomework.models.Department;
import com.endava.developement.java.webapphomework.models.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class EmployeeDAO {

    private final DataSource dataSource;
    private final DepartmentDAO departmentDAO;

    @Autowired
    public EmployeeDAO(DataSource dataSource, DepartmentDAO departmentDAO) {
        this.dataSource = dataSource;
        this.departmentDAO = departmentDAO;
    }

    public List<Employee> getAll() throws SQLException {
        try(Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM employees"))
        {
            List<Employee> responseList = new ArrayList<>();
            while (resultSet.next()) {
                Employee employee = new Employee();
                employee.setId(resultSet.getLong("id"));
                employee.setFirstName(resultSet.getString("first_name"));
                employee.setLastName(resultSet.getString("last_name"));
                employee.setEmail(resultSet.getString("email"));
                employee.setSalary(resultSet.getFloat("salary"));
                employee.setPhoneNumber(resultSet.getString("phone_number"));

                Department department = departmentDAO.findById(resultSet.getLong("department_id"));
                employee.setDepartment(department);

                responseList.add(employee);
            }

            statement.close();
            resultSet.close();
            connection.close();
            return responseList;
        }
    }

    public Employee findById(Long id) {
        try
        {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM employees WHERE id = ?");
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            Employee employee = new Employee();
            while (resultSet.next()) {
                employee.setId(resultSet.getLong("id"));
                employee.setFirstName(resultSet.getString("first_name"));
                employee.setLastName(resultSet.getString("last_name"));
                employee.setEmail(resultSet.getString("email"));
                employee.setSalary(resultSet.getFloat("salary"));
                employee.setPhoneNumber(resultSet.getString("phone_number"));

                Department department = departmentDAO.findById(resultSet.getLong("department_id"));
                employee.setDepartment(department);
            }

            statement.close();
            resultSet.close();
            connection.close();
            return employee;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public Employee save(Employee employee) {
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO employees(first_name, last_name, email, phone_number, salary, department_id) " +
                            "VALUES (?,?,?,?,?,?)");
            statement.setString(1,employee.getFirstName());
            statement.setString(2, employee.getLastName());
            statement.setString(3, employee.getEmail());
            statement.setString(4, employee.getPhoneNumber());
            statement.setFloat(5, employee.getSalary());
            statement.setLong(6, employee.getDepartment().getId());

            statement.executeUpdate();

            return findByEmail(employee.getEmail());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Employee findByEmail(String email) throws SQLException {

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM employees WHERE email = ?");
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            Employee employee = new Employee();
            while (resultSet.next()) {
                employee.setId(resultSet.getLong("id"));
                employee.setFirstName(resultSet.getString("first_name"));
                employee.setLastName(resultSet.getString("last_name"));
                employee.setEmail(resultSet.getString("email"));
                employee.setSalary(resultSet.getFloat("salary"));
                employee.setPhoneNumber(resultSet.getString("phone_number"));

                Department department = departmentDAO.findById(resultSet.getLong("department_id"));
                employee.setDepartment(department);
            }

            statement.close();
            resultSet.close();
            connection.close();
            return employee;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    public Employee saveAfterEdit(Employee existingEmployee) {
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                            "UPDATE employees " +
                            "SET first_name = ?, " + "last_name = ?, " +
                                    "email = ?, " + "phone_number = ?, " + "salary = ?, " +
                                    "department_id = ? " +
                            "WHERE id = ?;");
            statement.setString(1,existingEmployee.getFirstName());
            statement.setString(2, existingEmployee.getLastName());
            statement.setString(3, existingEmployee.getEmail());
            statement.setString(4, existingEmployee.getPhoneNumber());
            statement.setFloat(5, existingEmployee.getSalary());
            statement.setLong(6, existingEmployee.getDepartment().getId());
            statement.setLong(7, existingEmployee.getId());


            statement.executeUpdate();

            return findByEmail(existingEmployee.getEmail());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Employee> findByDepartment(Long id) {
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM employees WHERE department_id = ?");
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            List<Employee> employees = new ArrayList<>();
            while (resultSet.next()) {
                Employee employee = new Employee();
                employee.setId(resultSet.getLong("id"));
                employee.setFirstName(resultSet.getString("first_name"));
                employee.setLastName(resultSet.getString("last_name"));
                employee.setEmail(resultSet.getString("email"));
                employee.setSalary(resultSet.getFloat("salary"));
                employee.setPhoneNumber(resultSet.getString("phone_number"));

                Department department = departmentDAO.findById(resultSet.getLong("department_id"));
                employee.setDepartment(department);

                employees.add(employee);
            }

            statement.close();
            resultSet.close();
            connection.close();
            return employees;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}