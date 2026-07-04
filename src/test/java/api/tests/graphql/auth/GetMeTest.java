package api.tests.graphql.auth;

import api.base.BaseGraphQLTest;
import api.body.graphql.auth.AuthBodies;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Positive test for the `me` query — returns the currently authenticated user.
 * Requires the session cookie saved by {@link LoginGraphQLTest}.
 */
public class GetMeTest extends BaseGraphQLTest {

    @Test
    public void getMe() throws Exception {
        Response response = authRequest()
                .body(AuthBodies.me().toString())
                .when().post("")
                .then().extract().response();

        System.out.println("Response: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNull(response.jsonPath().get("errors"));

        Assert.assertNotNull(response.jsonPath().getString("data.me.id"), "me.id should not be null");
        Assert.assertNotNull(response.jsonPath().getString("data.me.email"), "me.email should not be null");
    }
}
