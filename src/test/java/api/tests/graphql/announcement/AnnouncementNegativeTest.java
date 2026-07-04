package api.tests.graphql.announcement;

import api.base.BaseGraphQLTest;
import api.body.graphql.GraphQLPayload;
import api.body.graphql.announcement.AnnouncementBodies;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import api.tests.graphql.NegativeAssertions;

/** Negative scenarios for the Announcement module. */
public class AnnouncementNegativeTest extends BaseGraphQLTest {

    private static final String NON_EXISTENT_UUID = "00000000-0000-0000-0000-000000000000";

    @Test
    public void getAnnouncementById_nonExistent() throws Exception {
        Response response = authRequest()
                .body(AnnouncementBodies.getById(NON_EXISTENT_UUID).toString())
                .when().post("").then().extract().response();
        System.out.println("getAnnouncementById_nonExistent: " + response.asString());
        NegativeAssertions.assertGraphQLError(response, "data.announcementById");
    }

    @Test
    public void updateAnnouncement_nonExistent() throws Exception {
        Response response = authRequest()
                .body(AnnouncementBodies.update(NON_EXISTENT_UUID, "x", "y").toString())
                .when().post("").then().extract().response();
        System.out.println("updateAnnouncement_nonExistent: " + response.asString());
        NegativeAssertions.assertGraphQLError(response, "data.updateAnnouncement");
    }

    @Test
    public void deleteAnnouncement_nonExistent() throws Exception {
        Response response = authRequest()
                .body(AnnouncementBodies.delete(NON_EXISTENT_UUID).toString())
                .when().post("").then().extract().response();
        System.out.println("deleteAnnouncement_nonExistent: " + response.asString());
        NegativeAssertions.assertGraphQLError(response, "data.deleteAnnouncement");
    }

    @Test
    public void createAnnouncement_missingRequiredFields() throws Exception {
        // AnnouncementInput.title and .content are NON_NULL.
        String q = "mutation CreateAnnouncement($input: AnnouncementInput!) { createAnnouncement(input: $input) { id } }";
        JSONObject input = new JSONObject();
        input.put("title", JSONObject.NULL);
        input.put("content", JSONObject.NULL);
        JSONObject vars = new JSONObject();
        vars.put("input", input);

        Response response = authRequest()
                .body(GraphQLPayload.of("CreateAnnouncement", q, vars).toString())
                .when().post("").then().extract().response();
        System.out.println("createAnnouncement_missingRequiredFields: " + response.asString());
        Assert.assertNotNull(response.jsonPath().get("errors"));
    }
}
