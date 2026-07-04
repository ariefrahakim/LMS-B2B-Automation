package api.tests.graphql.division;

import api.base.BaseGraphQLTest;
import api.body.graphql.GraphQLPayload;
import api.body.graphql.division.DivisionBodies;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import api.tests.graphql.NegativeAssertions;

/** Negative scenarios for the Division (class) module. */
public class DivisionNegativeTest extends BaseGraphQLTest {

    private static final String NON_EXISTENT_UUID = "00000000-0000-0000-0000-000000000000";

    @Test
    public void getDivisionById_nonExistent() throws Exception {
        Response response = authRequest()
                .body(DivisionBodies.getById(NON_EXISTENT_UUID).toString())
                .when().post("").then().extract().response();
        System.out.println("getDivisionById_nonExistent: " + response.asString());
        NegativeAssertions.assertGraphQLError(response, "data.divisionById");
    }

    @Test
    public void updateDivision_nonExistent() throws Exception {
        Response response = authRequest()
                .body(DivisionBodies.update(NON_EXISTENT_UUID, "x", "y").toString())
                .when().post("").then().extract().response();
        System.out.println("updateDivision_nonExistent: " + response.asString());
        NegativeAssertions.assertGraphQLError(response, "data.updateDivision");
    }

    @Test
    public void deleteDivision_nonExistent() throws Exception {
        Response response = authRequest()
                .body(DivisionBodies.delete(NON_EXISTENT_UUID).toString())
                .when().post("").then().extract().response();
        System.out.println("deleteDivision_nonExistent: " + response.asString());
        NegativeAssertions.assertGraphQLError(response, "data.deleteDivision");
    }

    @Test
    public void createDivision_missingRequiredName() throws Exception {
        // DivisionInput.name is NON_NULL — sending null must trigger a validation error.
        String q = "mutation CreateDivision($input: DivisionInput!) { createDivision(input: $input) { id } }";
        JSONObject input = new JSONObject();
        input.put("name", JSONObject.NULL);
        input.put("description", "no name");
        JSONObject vars = new JSONObject();
        vars.put("input", input);

        Response response = authRequest()
                .body(GraphQLPayload.of("CreateDivision", q, vars).toString())
                .when().post("").then().extract().response();
        System.out.println("createDivision_missingRequiredName: " + response.asString());
        Assert.assertNotNull(response.jsonPath().get("errors"));
    }
}
