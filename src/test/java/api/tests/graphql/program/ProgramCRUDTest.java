package api.tests.graphql.program;

import api.base.BaseGraphQLTest;
import api.body.graphql.program.ProgramBodies;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.Utils;

public class ProgramCRUDTest extends BaseGraphQLTest {

    private static String programId;

    @Test(priority = 1)
    public void createProgram() throws Exception {
        Response response = authRequest()
                .body(ProgramBodies.create("QA-Prog-" + Utils.generateRandomTitle(),
                        "QA Program Studi", "onboarding").toString())
                .when().post("")
                .then().extract().response();

        System.out.println("createProgram: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNull(response.jsonPath().get("errors"));

        programId = response.jsonPath().getString("data.createProgram.id");
        Assert.assertNotNull(programId);
    }

    @Test(priority = 2, dependsOnMethods = "createProgram")
    public void getProgramById() throws Exception {
        Response response = authRequest()
                .body(ProgramBodies.getById(programId).toString())
                .when().post("")
                .then().extract().response();

        System.out.println("programById: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNull(response.jsonPath().get("errors"));
        Assert.assertEquals(response.jsonPath().getString("data.programById.id"), programId);
    }

    @Test(priority = 3, dependsOnMethods = "createProgram")
    public void updateProgram() throws Exception {
        String newTitle = "QA-Prog-Updated-" + Utils.generateRandomTitle();
        Response response = authRequest()
                .body(ProgramBodies.update(programId, newTitle, "updated desc", "onboarding").toString())
                .when().post("")
                .then().extract().response();

        System.out.println("updateProgram: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNull(response.jsonPath().get("errors"));

        // Update mutation may echo pre-update snapshot; verify persistence via re-fetch.
        Response reload = authRequest()
                .body(ProgramBodies.getById(programId).toString())
                .when().post("").then().extract().response();
        Assert.assertEquals(reload.jsonPath().getString("data.programById.title"), newTitle,
                "New title must be persisted");
    }

    @Test(priority = 4, dependsOnMethods = "createProgram")
    public void deleteProgram() throws Exception {
        Response response = authRequest()
                .body(ProgramBodies.delete(programId).toString())
                .when().post("")
                .then().extract().response();

        System.out.println("deleteProgram: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNull(response.jsonPath().get("errors"));
    }
}
