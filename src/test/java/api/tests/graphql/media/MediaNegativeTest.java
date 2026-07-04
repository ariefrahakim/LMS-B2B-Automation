package api.tests.graphql.media;

import api.base.BaseGraphQLTest;
import api.body.graphql.media.MediaBodies;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import api.tests.graphql.NegativeAssertions;

/** Negative scenarios for the Media Library module (read-only). */
public class MediaNegativeTest extends BaseGraphQLTest {

    private static final String NON_EXISTENT_UUID = "00000000-0000-0000-0000-000000000000";

    @Test
    public void mediaById_nonExistent() throws Exception {
        Response response = authRequest()
                .body(MediaBodies.mediaById(NON_EXISTENT_UUID).toString())
                .when().post("").then().extract().response();
        System.out.println("mediaById_nonExistent: " + response.asString());
        NegativeAssertions.assertGraphQLError(response, "data.mediaById");
    }
}
