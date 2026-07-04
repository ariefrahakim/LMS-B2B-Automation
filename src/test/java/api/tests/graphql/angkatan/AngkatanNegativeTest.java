package api.tests.graphql.angkatan;

import api.base.BaseGraphQLTest;
import api.body.graphql.GraphQLPayload;
import api.body.graphql.angkatan.AngkatanBodies;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import api.tests.graphql.NegativeAssertions;

/**
 * Negative scenarios for the Angkatan (cohort) module. Each scenario targets a
 * distinct failure mode: missing NON_NULL field, wrong scalar type, and CRUD
 * operations against a non-existent id.
 */
public class AngkatanNegativeTest extends BaseGraphQLTest {

    private static final Number NON_EXISTENT_ID = 999_999_999;

    @Test
    public void createAngkatan_missingRequiredName() throws Exception {
        // `name` is declared NON_NULL in AngkatanInput → sending null must yield an error.
        String q = "mutation CreateAngkatan($input: AngkatanInput!) { createAngkatan(input: $input) { id } }";
        JSONObject input = new JSONObject();
        input.put("name", JSONObject.NULL);
        input.put("description", "no name");
        JSONObject vars = new JSONObject();
        vars.put("input", input);
        JSONObject payload = GraphQLPayload.of("CreateAngkatan", q, vars);

        Response response = authRequest().body(payload.toString()).when().post("").then().extract().response();
        System.out.println("createAngkatan_missingRequiredName: " + response.asString());
        int status = response.getStatusCode();
        Assert.assertTrue(status == 200 || status == 400);
        Assert.assertNotNull(response.jsonPath().get("errors"));
    }

    @Test
    public void createAngkatan_wrongVariableType() throws Exception {
        // Send an int for `name` (declared String) → schema validation must reject it.
        String q = "mutation CreateAngkatan($input: AngkatanInput!) { createAngkatan(input: $input) { id } }";
        JSONObject input = new JSONObject();
        input.put("name", 12345);
        input.put("description", "desc");
        JSONObject vars = new JSONObject();
        vars.put("input", input);

        Response response = authRequest()
                .body(GraphQLPayload.of("CreateAngkatan", q, vars).toString())
                .when().post("").then().extract().response();
        System.out.println("createAngkatan_wrongVariableType: " + response.asString());
        Assert.assertNotNull(response.jsonPath().get("errors"));
    }

    @Test
    public void getAngkatanById_nonExistent() throws Exception {
        Response response = authRequest()
                .body(AngkatanBodies.byId(NON_EXISTENT_ID).toString())
                .when().post("").then().extract().response();
        System.out.println("getAngkatanById_nonExistent: " + response.asString());
        NegativeAssertions.assertGraphQLError(response, "data.angkatanById");
    }

    @Test
    public void updateAngkatan_nonExistent() throws Exception {
        Response response = authRequest()
                .body(AngkatanBodies.update(NON_EXISTENT_ID, "x", "x").toString())
                .when().post("").then().extract().response();
        System.out.println("updateAngkatan_nonExistent: " + response.asString());
        NegativeAssertions.assertGraphQLError(response, "data.updateAngkatan");
    }

    @Test
    public void deleteAngkatan_nonExistent() throws Exception {
        Response response = authRequest()
                .body(AngkatanBodies.delete(NON_EXISTENT_ID).toString())
                .when().post("").then().extract().response();
        System.out.println("deleteAngkatan_nonExistent: " + response.asString());
        NegativeAssertions.assertGraphQLError(response, "data.deleteAngkatan");
    }
}
