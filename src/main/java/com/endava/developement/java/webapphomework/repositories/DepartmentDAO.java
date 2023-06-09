package com.endava.developement.java.webapphomework.repositories;

import com.endava.developement.java.webapphomework.exceptions.DepartmentNotFoundException;
import com.endava.developement.java.webapphomework.models.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DepartmentDAO {

    private final DataSource dataSource;


    @Autowired
    public DepartmentDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public List<Department> findAll(){
        try{
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM departments;");
            ResultSet set = statement.executeQuery();
            List<Department> departments = new ArrayList<>();
            while(set.next()){
                Department department = new Department();
                department.setId(set.getLong("id"));
                department.setName(set.getString("name"));
                department.setLocation(set.getString("location"));
                departments.add(department);
            }

            statement.close();
            set.close();
            connection.close();

            return departments;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Department findById(Long s) {
        try{
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM departments WHERE id = ?");
            statement.setLong(1, s);
            Department department = fromResultSetToDepartment(statement.executeQuery());

            statement.close();
            connection.close();

            return department;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Department findByName(String s) {
        try{
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM departments WHERE name = ?");
            statement.setString(1, s);
            Department department = fromResultSetToDepartment(statement.executeQuery());

            statement.close();
            connection.close();

            return department;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public Department save(Department department) {
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO departments(name, location) " +
                            "VALUES (?,?)");
            statement.setString(1,department.getName());
            statement.setString(2, department.getLocation());
            statement.executeUpdate();

            return findByName(department.getName());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Department edit(Department department) {
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE departments SET location = ?, name = ? " +
                            "WHERE id = ?");
            statement.setString(1,department.getLocation());
            statement.setString(2, department.getName());
            statement.setLong(3, department.getId());
            statement.executeUpdate();

            return findByName(department.getName());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Department fromResultSetToDepartment(ResultSet set) throws SQLException {
        Department department = new Department();
        while(set.next()){
            department.setId(set.getLong("id"));
            department.setName(set.getString("name"));
            department.setLocation(set.getString("location"));
        }

        if (department.getId() == null) throw new DepartmentNotFoundException();

        set.close();

        return department;
    }
}
