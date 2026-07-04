package api.tests.graphql.employee;

import api.base.BaseGraphQLTest;
import api.body.graphql.GraphQLPayload;
import api.body.graphql.employee.EmployeeBodies;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import api.tests.graphql.NegativeAssertions;

/**
 * Negative scenarios for the Employee (User) module.
 */
public class EmployeeNegativeTest extends BaseGraphQLTest {

    private static final String NON_EXISTENT_UUID = "00000000-0000-0000-0000-000000000000";

    @Test
    public void getEmployeeById_nonExistent() throws Exception {
        Response response = authRequest()
                .body(EmployeeBodies.getById(NON_EXISTENT_UUID).toString())
                .when().post("").then().extract().response();
        System.out.println("getEmployeeById_nonExistent: " + response.asString());
        NegativeAssertions.assertGraphQLError(response, "data.employeeById");
    }

    @Test
    public void updateEmployee_nonExistent() throws Exception {
        Response response = authRequest()
                .body(EmployeeBodies.update(NON_EXISTENT_UUID, "Nobody", "no@example.com", "employee").toString())
                .when().post("").then().extract().response();
        System.out.println("updateEmployee_nonExistent: " + response.asString());
        NegativeAssertions.assertGraphQLError(response, "data.updateEmployee");
    }

    @Test
    public void deleteEmployee_nonExistent() throws Exception {
        Response response = authRequest()
                .body(EmployeeBodies.delete(NON_EXISTENT_UUID).toString())
                .when().post("").then().extract().response();
        System.out.println("deleteEmployee_nonExistent: " + response.asString());
        NegativeAssertions.assertGraphQLError(response, "data.deleteEmployee");
    }

    @Test
    public void createEmployee_duplicateEmail() throws Exception {
        // The configured admin user email is guaranteed to already exist in the DB,
        // so creating another employee with the same address must be rejected as a
        // duplicate.
        Response response = authRequest()
                .body(EmployeeBodies.create("Duplicate",
                        utils.ConfigReader.getProperty("emailWeb"), "employee").toString())
                .when().post("").then().extract().response();
        System.out.println("createEmployee_duplicateEmail: " + response.asString());
        NegativeAssertions.assertGraphQLError(response, "data.createEmployee");
    }

    @Test
    public void createEmployee_wrongVariableType() throws Exception {
        // Send name as a number instead of a string.
        String q = "mutation CreateEmployee($input: EmployeeInput!) { createEmployee(input: $input) { id } }";
        JSONObject input = new JSONObject();
        input.put("name", 123);
        input.put("email", "bogus@example.com");
        input.put("employeeRole", "employee");
        JSONObject vars = new JSONObject();
        vars.put("input", input);

        Response response = authRequest()
                .body(GraphQLPayload.of("CreateEmployee", q, vars).toString())
                .when().post("").then().extract().response();
        System.out.println("createEmployee_wrongVariableType: " + response.asString());
        Assert.assertNotNull(response.jsonPath().get("errors"));
    }
}
