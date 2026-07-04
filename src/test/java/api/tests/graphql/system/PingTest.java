package api.tests.graphql.system;

import api.base.BaseGraphQLTest;
import api.body.graphql.auth.AuthBodies;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * System-level smoke test: the `ping` query is unauthenticated and must always
 * return the string "pong". If this fails, the endpoint itself is down.
 */
public class PingTest extends BaseGraphQLTest {

    @Test
    public void ping() {
        Response response = baseRequest()
                .body(AuthBodies.ping().toString())
                .when().post("")
                .then().extract().response();

        System.out.println("Response: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNull(response.jsonPath().get("errors"));
        Assert.assertEquals(response.jsonPath().getString("data.ping"), "pong");
    }
}
