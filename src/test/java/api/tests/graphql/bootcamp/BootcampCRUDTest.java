package api.tests.graphql.bootcamp;

import api.base.BaseGraphQLTest;
import api.body.graphql.bootcamp.BootcampBodies;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.Utils;

public class BootcampCRUDTest extends BaseGraphQLTest {

    private static String bootcampId;

    private static final String START = Utils.getDateAfterFourDays();
    private static final String END = Utils.getDateAfterSevenDays();

    @Test(priority = 1)
    public void createBootcamp() throws Exception {
        Response response = authRequest()
                .body(BootcampBodies.create("QA-FastTrack-" + Utils.generateRandomTitle(),
                        "QA Fast Track Class", START, END).toString())
                .when().post("")
                .then().extract().response();

        System.out.println("createBootcamp: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNull(response.jsonPath().get("errors"));

        bootcampId = response.jsonPath().getString("data.createBootcamp.id");
        Assert.assertNotNull(bootcampId);
    }

    @Test(priority = 2, dependsOnMethods = "createBootcamp")
    public void getBootcampById() throws Exception {
        Response response = authRequest()
                .body(BootcampBodies.getById(bootcampId).toString())
                .when().post("")
                .then().extract().response();

        System.out.println("bootcampById: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNull(response.jsonPath().get("errors"));
        Assert.assertEquals(response.jsonPath().getString("data.bootcampById.id"), bootcampId);
    }

    @Test(priority = 3, dependsOnMethods = "createBootcamp")
    public void updateBootcamp() throws Exception {
        String newTitle = "QA-FastTrack-Updated-" + Utils.generateRandomTitle();
        Response response = authRequest()
                .body(BootcampBodies.update(bootcampId, newTitle, "updated bootcamp", START, END).toString())
                .when().post("")
                .then().extract().response();

        System.out.println("updateBootcamp: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNull(response.jsonPath().get("errors"));
        Assert.assertEquals(response.jsonPath().getString("data.updateBootcamp.title"), newTitle);
    }

    @Test(priority = 4, dependsOnMethods = "createBootcamp")
    public void deleteBootcamp() throws Exception {
        Response response = authRequest()
                .body(BootcampBodies.delete(bootcampId).toString())
                .when().post("")
                .then().extract().response();

        System.out.println("deleteBootcamp: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNull(response.jsonPath().get("errors"));
    }
}
