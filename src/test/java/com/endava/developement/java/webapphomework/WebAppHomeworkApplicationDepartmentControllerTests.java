package com.endava.developement.java.webapphomework;

import com.endava.developement.java.webapphomework.DTO.DepartmentResponse;
import com.endava.developement.java.webapphomework.exceptions.exceptionHandling.ErrorDetails;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebAppHomeworkApplicationDepartmentControllerTests {

    private final TestRestTemplate testRestTemplate;

    private final DataSource dataSource;


    @Autowired
    WebAppHomeworkApplicationDepartmentControllerTests(TestRestTemplate testRestTemplate, DataSource dataSource) {
        this.testRestTemplate = testRestTemplate;
        this.dataSource = dataSource;
    }


    @Test
    void testGetAll(){

        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        int count = 0;

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM departments");
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                count++;
            }
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        HttpEntity request = new HttpEntity(headers);

        // Act
        ResponseEntity<List<DepartmentResponse>> response = testRestTemplate.exchange("/departments",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {
                });

        List<DepartmentResponse> responseList = response.getBody();

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(responseList);
        Assertions.assertEquals(count, responseList.size());
    }


    @Test
    void testGetOne_WhenValidIdProvided(){

        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        DepartmentResponse fromGet = new DepartmentResponse();

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM departments WHERE id = ?");
            statement.setLong(1, 2);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                fromGet.setId(resultSet.getLong("id"));
                fromGet.setLocation(resultSet.getString("location"));
                fromGet.setName(resultSet.getString("name"));
            }
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        HttpEntity request = new HttpEntity(headers);

        // Act
        ResponseEntity<DepartmentResponse> response = testRestTemplate.exchange("/departments/2",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {
                });

        DepartmentResponse responseBody = response.getBody();

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(responseBody);
        Assertions.assertEquals(fromGet.getId(), responseBody.getId());
        Assertions.assertEquals(fromGet.getName(), responseBody.getName());
        Assertions.assertEquals(fromGet.getLocation(), responseBody.getLocation());
    }


    @Test
    void testGetOne_WhenNotValidIdProvided_ThenExceptionWillBeThrown(){

        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity request = new HttpEntity(headers);

        // Act
        ResponseEntity<ErrorDetails> response = testRestTemplate.exchange("/departments/100",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {
                });

        ErrorDetails responseBody = response.getBody();

        // Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertNotNull(responseBody);
        Assertions.assertEquals("You typed department id which is not registered in the system!", responseBody.getMessage());
    }

    @Test
    void testCreateDepartment_WhenValidDetailsProvided_returnsDepartmentDetails() throws JSONException {
        // Arrange
        JSONObject departmentDetailsRequestJson = new JSONObject();
        departmentDetailsRequestJson.put("name", "Sales");
        departmentDetailsRequestJson.put("location", "New-York");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(departmentDetailsRequestJson.toString(), headers);

        // Act
        ResponseEntity<DepartmentResponse> response = testRestTemplate.postForEntity("/departments", request, DepartmentResponse.class);
        DepartmentResponse responseEntity = response.getBody();


        // Delete From DB
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement("DELETE FROM departments WHERE id = ?");
            statement.setLong(1, responseEntity.getId());
            statement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        // Assert
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(departmentDetailsRequestJson.getString("name"), responseEntity.getName());
        Assertions.assertEquals(departmentDetailsRequestJson.getString("location"), responseEntity.getLocation());
    }


    @Test
    void testCreateDepartment_WhenNotValidDetailsProvided_returnsErrorDetails() throws JSONException {
        // Arrange
        JSONObject departmentDetailsRequestJson = new JSONObject();
        departmentDetailsRequestJson.put("name", "Sales");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(departmentDetailsRequestJson.toString(), headers);

        // Act
        ResponseEntity<ErrorDetails> response = testRestTemplate.postForEntity("/departments", request, ErrorDetails.class);
        ErrorDetails responseEntity = response.getBody();


        // Assert
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertNotNull(responseEntity);
        Assertions.assertTrue(responseEntity.getMessage().contains("could not execute statement [ERROR: null value in column \"location\" of relation \"departments\" violates not-null constraint"));
    }


    @Test
    void testPutMethod_WhenValidDataProvided_ThenEditDatabase() throws JSONException {
        // Arrange
        HttpHeaders getHeaders = new HttpHeaders();
        getHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity getRequest = new HttpEntity(getHeaders);


        JSONObject departmentDetailsRequestJson = new JSONObject();
        departmentDetailsRequestJson.put("name", "Sales");
        departmentDetailsRequestJson.put("location", "New-York");

        HttpHeaders putHeaders = new HttpHeaders();
        putHeaders.setContentType(MediaType.APPLICATION_JSON);
        putHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> putRequest = new HttpEntity<>(departmentDetailsRequestJson.toString(), putHeaders);


        // Act

        // Perform GET query
        ResponseEntity<DepartmentResponse> getResponse = testRestTemplate.exchange("/departments/2",
                HttpMethod.GET,
                getRequest,
                new ParameterizedTypeReference<>() {
                });

        DepartmentResponse getResponseBody = getResponse.getBody();

        // Perform PUT query
        ResponseEntity<DepartmentResponse> response = testRestTemplate.exchange(
                "/departments/2",
                HttpMethod.PUT,
                putRequest,
                DepartmentResponse.class);
        DepartmentResponse responseEntity = response.getBody();


        // Perform GET query
        ResponseEntity<DepartmentResponse> getAfterPutResponse = testRestTemplate.exchange("/departments/2",
                HttpMethod.GET,
                getRequest,
                new ParameterizedTypeReference<>() {
                });

        DepartmentResponse getAfterPutBody = getAfterPutResponse.getBody();

        // Perform PUT query
        HttpEntity<DepartmentResponse> finalPutRequest = new HttpEntity<>(getResponseBody, putHeaders);

        ResponseEntity<DepartmentResponse> rollbackChanges = testRestTemplate.exchange(
                "/departments/2",
                HttpMethod.PUT,
                finalPutRequest,
                DepartmentResponse.class);


        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(responseEntity);
        Assertions.assertNotNull(getAfterPutBody);
        Assertions.assertEquals(getAfterPutBody.getName(), departmentDetailsRequestJson.getString("name"));
        Assertions.assertEquals(getAfterPutBody.getLocation(), departmentDetailsRequestJson.getString("location"));
    }

    @Test
    void testPutMethod_WhenNotValidIdProvided_ThenErrorDetailsWillBeReturned() throws JSONException {
        // Arrange
        JSONObject departmentDetailsRequestJson = new JSONObject();
        departmentDetailsRequestJson.put("name", "Sales");
        departmentDetailsRequestJson.put("location", "New-York");

        HttpHeaders putHeaders = new HttpHeaders();
        putHeaders.setContentType(MediaType.APPLICATION_JSON);
        putHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> putRequest = new HttpEntity<>(departmentDetailsRequestJson.toString(), putHeaders);


        // Act

        // Perform PUT query
        ResponseEntity<ErrorDetails> response = testRestTemplate.exchange(
                "/departments/20",
                HttpMethod.PUT,
                putRequest,
                ErrorDetails.class);
        ErrorDetails responseEntity = response.getBody();

        // Assertions
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals("You typed department id which is not registered in the system!", responseEntity.getMessage());
    }

    @Test
    void testPutMethod_WhenNotValidDataProvided_ThenErrorDetailsWillBeReturned() throws JSONException {
        // Arrange
        JSONObject departmentDetailsRequestJson = new JSONObject();
        departmentDetailsRequestJson.put("location", "New-York");

        HttpHeaders putHeaders = new HttpHeaders();
        putHeaders.setContentType(MediaType.APPLICATION_JSON);
        putHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> putRequest = new HttpEntity<>(departmentDetailsRequestJson.toString(), putHeaders);


        // Act

        // Perform PUT query
        ResponseEntity<ErrorDetails> response = testRestTemplate.exchange(
                "/departments/2",
                HttpMethod.PUT,
                putRequest,
                ErrorDetails.class);
        ErrorDetails responseEntity = response.getBody();

        // Assertions
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertNotNull(responseEntity);
        Assertions.assertTrue(responseEntity.getMessage().contains("null value in column \"name\" of relation \"departments\" violates not-null constraint"));
    }

}
