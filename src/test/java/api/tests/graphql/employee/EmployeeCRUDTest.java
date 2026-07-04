package api.tests.graphql.employee;

import api.base.BaseGraphQLTest;
import api.body.graphql.employee.EmployeeBodies;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.Utils;

public class EmployeeCRUDTest extends BaseGraphQLTest {

    private static String employeeId;
    private static String createdEmail;

    @Test(priority = 1)
    public void createEmployee() throws Exception {
        String suffix = Utils.generateRandomTitle();
        String name = "QA-Emp-" + suffix;
        createdEmail = "qa-emp-" + suffix.toLowerCase() + "@example.com";

        Response response = authRequest()
                .body(EmployeeBodies.create(name, createdEmail, "employee").toString())
                .when().post("")
                .then().extract().response();

        System.out.println("createEmployee: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNull(response.jsonPath().get("errors"));

        employeeId = response.jsonPath().getString("data.createEmployee.id");
        Assert.assertNotNull(employeeId);
        Assert.assertEquals(response.jsonPath().getString("data.createEmployee.email"), createdEmail);
    }

    @Test(priority = 2, dependsOnMethods = "createEmployee")
    public void getEmployeeById() throws Exception {
        Response response = authRequest()
                .body(EmployeeBodies.getById(employeeId).toString())
                .when().post("")
                .then().extract().response();

        System.out.println("employeeById: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNull(response.jsonPath().get("errors"));
        Assert.assertEquals(response.jsonPath().getString("data.employeeById.id"), employeeId);
    }

    @Test(priority = 3, dependsOnMethods = "createEmployee")
    public void updateEmployee() throws Exception {
        String newName = "QA-Emp-Updated-" + Utils.generateRandomTitle();
        Response response = authRequest()
                .body(EmployeeBodies.update(employeeId, newName, createdEmail, "employee").toString())
                .when().post("")
                .then().extract().response();

        System.out.println("updateEmployee: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNull(response.jsonPath().get("errors"));
        Assert.assertEquals(response.jsonPath().getString("data.updateEmployee.id"), employeeId);

        // Verify persistence by re-fetching (the update mutation may return the pre-update snapshot).
        Response reload = authRequest()
                .body(EmployeeBodies.getById(employeeId).toString())
                .when().post("").then().extract().response();
        Assert.assertEquals(reload.jsonPath().getString("data.employeeById.name"), newName,
                "New name must be persisted");
    }

    @Test(priority = 4, dependsOnMethods = "createEmployee")
    public void deleteEmployee() throws Exception {
        Response response = authRequest()
                .body(EmployeeBodies.delete(employeeId).toString())
                .when().post("")
                .then().extract().response();

        System.out.println("deleteEmployee: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNull(response.jsonPath().get("errors"));
    }
}
