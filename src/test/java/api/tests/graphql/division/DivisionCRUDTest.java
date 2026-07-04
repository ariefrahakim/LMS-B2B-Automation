package api.tests.graphql.division;

import api.base.BaseGraphQLTest;
import api.body.graphql.division.DivisionBodies;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.Utils;

public class DivisionCRUDTest extends BaseGraphQLTest {

    private static String divisionId;

    @Test(priority = 1)
    public void createDivision() throws Exception {
        Response response = authRequest()
                .body(DivisionBodies.create("QA-Div-" + Utils.generateRandomTitle(),
                        "QA Division/Class").toString())
                .when().post("")
                .then().extract().response();

        System.out.println("createDivision: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNull(response.jsonPath().get("errors"));

        divisionId = response.jsonPath().getString("data.createDivision.id");
        Assert.assertNotNull(divisionId);
    }

    @Test(priority = 2, dependsOnMethods = "createDivision")
    public void getDivisionById() throws Exception {
        Response response = authRequest()
                .body(DivisionBodies.getById(divisionId).toString())
                .when().post("")
                .then().extract().response();

        System.out.println("divisionById: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNull(response.jsonPath().get("errors"));
        Assert.assertEquals(response.jsonPath().getString("data.divisionById.id"), divisionId);
    }

    @Test(priority = 3, dependsOnMethods = "createDivision")
    public void updateDivision() throws Exception {
        String newName = "QA-Div-Updated-" + Utils.generateRandomTitle();
        Response response = authRequest()
                .body(DivisionBodies.update(divisionId, newName, "updated desc").toString())
                .when().post("")
                .then().extract().response();

        System.out.println("updateDivision: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNull(response.jsonPath().get("errors"));

        // Update mutation may echo the pre-update state; verify persistence via a re-fetch.
        Response reload = authRequest()
                .body(DivisionBodies.getById(divisionId).toString())
                .when().post("").then().extract().response();
        Assert.assertEquals(reload.jsonPath().getString("data.divisionById.name"), newName,
                "New name must be persisted");
    }

    @Test(priority = 4, dependsOnMethods = "createDivision")
    public void deleteDivision() throws Exception {
        Response response = authRequest()
                .body(DivisionBodies.delete(divisionId).toString())
                .when().post("")
                .then().extract().response();

        System.out.println("deleteDivision: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNull(response.jsonPath().get("errors"));
    }
}
