package com.endava.developement.java.webapphomework;

import com.endava.developement.java.webapphomework.DTO.EmployeeResponse;
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
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebAppHomeworkApplicationEmployeeControllerTests {

	private final TestRestTemplate testRestTemplate;

	private final DataSource dataSource;


	@Autowired
	WebAppHomeworkApplicationEmployeeControllerTests(TestRestTemplate testRestTemplate, DataSource dataSource) {
		this.testRestTemplate = testRestTemplate;
		this.dataSource = dataSource;
	}


	@Test
	void testGetAllEmployees(){
		// Arrange
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		int count = 0;

		try {
			Connection connection = dataSource.getConnection();
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM employees");
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
		ResponseEntity<List<EmployeeResponse>> response = testRestTemplate.exchange("/employees",
				HttpMethod.GET,
				request,
				new ParameterizedTypeReference<>() {
				});

		List<EmployeeResponse> responseList = response.getBody();

		// Assert
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(responseList);
		Assertions.assertEquals(count, responseList.size());
	}

	@Test
	void testGetOneEmployee_WhenValidIdProvided_ReturnsEmployee() {
		// Arrange
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity request = new HttpEntity(headers);

		// Act
		ResponseEntity<EmployeeResponse> response = testRestTemplate.exchange("/employees/1",
				HttpMethod.GET,
				request,
				new ParameterizedTypeReference<>() {
				});

		EmployeeResponse employeeResponse = response.getBody();

		// Assert
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(employeeResponse);
		Assertions.assertEquals("Nikita", employeeResponse.getFirstName());
		Assertions.assertEquals("Budeanski", employeeResponse.getLastName());
		Assertions.assertEquals("nikbud03@gmail.com", employeeResponse.getEmail());
		Assertions.assertEquals("060742901", employeeResponse.getPhoneNumber());
		Assertions.assertEquals(1500.0, employeeResponse.getSalary().floatValue());
		Assertions.assertEquals("IT", employeeResponse.getDepartmentName());
	}

	@Test
	void testGetOneEmployee_WhenNonValidIdProvided_ReturnsErrorDetails() {
		// Arrange
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity request = new HttpEntity(headers);

		// Act
		ResponseEntity<ErrorDetails> response = testRestTemplate.exchange("/employees/1000",
				HttpMethod.GET,
				request,
				new ParameterizedTypeReference<>() {
				});

		ErrorDetails errorDetails = response.getBody();

		// Assert
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		Assertions.assertNotNull(errorDetails);
		Assertions.assertEquals("No such employee in the system !", errorDetails.getMessage());
	}

	@Test
	void testCreateEmployee_WhenValidDetailsProvided_returnsUserDetails() throws JSONException {
		// Arrange
		JSONObject userDetailsRequestJson = new JSONObject();
		userDetailsRequestJson.put("firstName", "Artiom");
		userDetailsRequestJson.put("lastName", "Buga");
		userDetailsRequestJson.put("email", "bugaArtiom@mail.ru");
		userDetailsRequestJson.put("phoneNumber", "061920183");
		userDetailsRequestJson.put("salary", 1900.0);
		userDetailsRequestJson.put("departmentName", "Marketing");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));

		HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<EmployeeResponse> response = testRestTemplate.postForEntity("/employees", request, EmployeeResponse.class);
		EmployeeResponse responseEntity = response.getBody();


		// Delete From DB
		try {
			Connection connection = dataSource.getConnection();
			PreparedStatement statement = connection.prepareStatement("DELETE FROM employees WHERE id = ?");
			statement.setLong(1, responseEntity.getId());
			statement.executeUpdate();
			connection.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}


		// Assert
		Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
		Assertions.assertNotNull(responseEntity);
		Assertions.assertEquals(userDetailsRequestJson.getString("firstName"), responseEntity.getFirstName());
		Assertions.assertEquals(userDetailsRequestJson.getString("lastName"), responseEntity.getLastName());
		Assertions.assertEquals(userDetailsRequestJson.getString("email"), responseEntity.getEmail());
		Assertions.assertEquals(userDetailsRequestJson.getString("phoneNumber"), responseEntity.getPhoneNumber());
		Assertions.assertEquals(1900.0, responseEntity.getSalary().floatValue());
		Assertions.assertEquals(userDetailsRequestJson.getString("departmentName"), responseEntity.getDepartmentName());

	}

	@Test
	void testCreateEmployee_WhenNoRequiredFieldInProvidedJSON_throwsAnError() throws JSONException {
		// Arrange
		JSONObject userDetailsRequestJson = new JSONObject();
		userDetailsRequestJson.put("firstName", "Boris");
		userDetailsRequestJson.put("lastName", "Polikov");
		userDetailsRequestJson.put("phoneNumber", "070391730");
		userDetailsRequestJson.put("salary", 2000.0);
		userDetailsRequestJson.put("departmentName", "IT");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));

		HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);


		// Act
		ResponseEntity<ErrorDetails> response = testRestTemplate.postForEntity("/employees", request, ErrorDetails.class);
		ErrorDetails responseEntity = response.getBody();


		// Assert
		Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		Assertions.assertNotNull(responseEntity);
		Assertions.assertTrue(responseEntity.getMessage().contains("ConstraintViolationImpl"));
		Assertions.assertTrue(responseEntity.getMessage().contains("propertyPath=email"));
		Assertions.assertTrue(responseEntity.getMessage().contains("must not be null"));

	}



	@Test
	@Transactional
	void testPutMethod_WhenValidDetailsProvided_ThenReturnEditedEmployee() throws JSONException {
		// Arrange
		HttpHeaders getHeaders = new HttpHeaders();
		getHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity getRequest = new HttpEntity(getHeaders);

		JSONObject userDetailsRequestJson = new JSONObject();
		userDetailsRequestJson.put("firstName", "Anna");
		userDetailsRequestJson.put("lastName", "Kondraieva");
		userDetailsRequestJson.put("email", "kondrAnna@gmail.com");
		userDetailsRequestJson.put("phoneNumber", "070649888");
		userDetailsRequestJson.put("salary", 1100.0);
		userDetailsRequestJson.put("departmentName", "Marketing");


		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));

		HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

		// Act

		// Perform Get Query
		ResponseEntity<EmployeeResponse> firstGetQuery = testRestTemplate.exchange("/employees/36",
				HttpMethod.GET,
				getRequest,
				new ParameterizedTypeReference<>() {
				});

		EmployeeResponse firstGetResponse = firstGetQuery.getBody();

		// Perform PUT query
		ResponseEntity<EmployeeResponse> response = testRestTemplate.exchange(
				"/employees/36",
				HttpMethod.PUT,
				request,
				EmployeeResponse.class);
		EmployeeResponse responseEntity = response.getBody();


		// Perform Get Query
		ResponseEntity<EmployeeResponse> performGetQuery = testRestTemplate.exchange("/employees/36",
				HttpMethod.GET,
				getRequest,
				new ParameterizedTypeReference<>() {
				});

		EmployeeResponse fromGet = performGetQuery.getBody();


		// Perform PUT query
		HttpEntity<EmployeeResponse> finalPutRequest = new HttpEntity<>(firstGetResponse, headers);


		ResponseEntity<EmployeeResponse> finalPutResponse = testRestTemplate.exchange(
				"/employees/36",
				HttpMethod.PUT,
				finalPutRequest,
				EmployeeResponse.class);
		EmployeeResponse finalPutResponseBody = finalPutResponse.getBody();

		// Assert
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(responseEntity);
		Assertions.assertNotNull(fromGet);
		Assertions.assertEquals(userDetailsRequestJson.getString("firstName"), fromGet.getFirstName());
		Assertions.assertEquals(userDetailsRequestJson.getString("lastName"), fromGet.getLastName());
		Assertions.assertEquals(userDetailsRequestJson.getString("email"), fromGet.getEmail());
		Assertions.assertEquals(userDetailsRequestJson.getString("phoneNumber"), fromGet.getPhoneNumber());
		Assertions.assertEquals(1100, fromGet.getSalary().floatValue());
		Assertions.assertEquals(userDetailsRequestJson.getString("departmentName"), fromGet.getDepartmentName());
	}

	@Test
	void testPutMethod_WhenInvalidEmployeeIdProvided_ThenInternalServerError() throws JSONException {
		// Arrange
		JSONObject userDetailsRequestJson = new JSONObject();
		userDetailsRequestJson.put("firstName", "Boris");
		userDetailsRequestJson.put("lastName", "Polikov");
		userDetailsRequestJson.put("email", "polikovBoris@mail.ru");
		userDetailsRequestJson.put("phoneNumber", "070649104");
		userDetailsRequestJson.put("salary", 2200.0);
		userDetailsRequestJson.put("departmentName", "IT");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));

		HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

		// Act

		// Perform PUT query
		ResponseEntity<ErrorDetails> response = testRestTemplate.exchange(
				"/employees/30",
				HttpMethod.PUT,
				request,
				ErrorDetails.class);
		ErrorDetails responseEntity = response.getBody();

		// Assert
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		Assertions.assertNotNull(responseEntity);
		Assertions.assertEquals("No such employee in the system !", responseEntity.getMessage());

	}

	@Test
	void testPutMethod_WhenInvalidDepartmentNameProvided_ThenInternalServerError() throws JSONException {
		// Arrange
		JSONObject userDetailsRequestJson = new JSONObject();
		userDetailsRequestJson.put("firstName", "Boris");
		userDetailsRequestJson.put("lastName", "Polikov");
		userDetailsRequestJson.put("email", "polikovBoris@mail.ru");
		userDetailsRequestJson.put("phoneNumber", "070649104");
		userDetailsRequestJson.put("salary", 2200.0);
		userDetailsRequestJson.put("departmentName", "Account");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));

		HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

		// Act

		// Perform PUT query
		ResponseEntity<ErrorDetails> response = testRestTemplate.exchange(
				"/employees/2",
				HttpMethod.PUT,
				request,
				ErrorDetails.class);
		ErrorDetails responseEntity = response.getBody();

		// Assert
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		Assertions.assertNotNull(responseEntity);
		Assertions.assertEquals("You typed department id which is not registered in the system!", responseEntity.getMessage());

	}

}
