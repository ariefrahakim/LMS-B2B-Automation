package api.tests.graphql.program;

import api.base.BaseGraphQLTest;
import api.body.graphql.GraphQLPayload;
import api.body.graphql.program.ProgramBodies;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import api.tests.graphql.NegativeAssertions;

/** Negative scenarios for the Program (Program Studi) module. */
public class ProgramNegativeTest extends BaseGraphQLTest {

    private static final String NON_EXISTENT_UUID = "00000000-0000-0000-0000-000000000000";

    @Test
    public void getProgramById_nonExistent() throws Exception {
        Response response = authRequest()
                .body(ProgramBodies.getById(NON_EXISTENT_UUID).toString())
                .when().post("").then().extract().response();
        System.out.println("getProgramById_nonExistent: " + response.asString());
        NegativeAssertions.assertGraphQLError(response, "data.programById");
    }

    @Test
    public void updateProgram_nonExistent() throws Exception {
        Response response = authRequest()
                .body(ProgramBodies.update(NON_EXISTENT_UUID, "x", "y", "onboarding").toString())
                .when().post("").then().extract().response();
        System.out.println("updateProgram_nonExistent: " + response.asString());
        NegativeAssertions.assertGraphQLError(response, "data.updateProgram");
    }

    @Test
    public void deleteProgram_nonExistent() throws Exception {
        Response response = authRequest()
                .body(ProgramBodies.delete(NON_EXISTENT_UUID).toString())
                .when().post("").then().extract().response();
        System.out.println("deleteProgram_nonExistent: " + response.asString());
        NegativeAssertions.assertGraphQLError(response, "data.deleteProgram");
    }

    @Test
    public void createProgram_missingRequiredTitle() throws Exception {
        // ProgramInput.title & .type are NON_NULL — omit title.
        String q = "mutation CreateProgram($input: ProgramInput!) { createProgram(input: $input) { id } }";
        JSONObject input = new JSONObject();
        input.put("title", JSONObject.NULL);
        input.put("type", "onboarding");
        JSONObject vars = new JSONObject();
        vars.put("input", input);

        Response response = authRequest()
                .body(GraphQLPayload.of("CreateProgram", q, vars).toString())
                .when().post("").then().extract().response();
        System.out.println("createProgram_missingRequiredTitle: " + response.asString());
        Assert.assertNotNull(response.jsonPath().get("errors"));
    }
}
