package api.tests.graphql.auth;

import api.base.BaseGraphQLTest;
import api.body.graphql.auth.AuthBodies;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Positive test for the `login` mutation.
 *
 * A successful login sets an HTTP session cookie (`sid_b2b`). We persist that
 * cookie to `graphql_session.json` on disk so that every downstream test can
 * pick it up without re-authenticating.
 */
public class LoginGraphQLTest extends BaseGraphQLTest {

    @Test
    public void login() throws IOException {
        // Send the login mutation with credentials read from config.properties.
        Response response = baseRequest()
                .body(AuthBodies.login().toString())
                .when().post("")
                .then().extract().response();

        System.out.println("Response: " + response.asString());

        // 1. Transport succeeded.
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        // 2. No top-level GraphQL errors.
        Assert.assertNull(response.jsonPath().get("errors"), "GraphQL errors should be null");
        // 3. The `login.errors` field (business-level errors) is also empty.
        Assert.assertNull(response.jsonPath().get("data.login.errors"), "Login field errors should be null");

        // 4. The user block is populated.
        String userId = response.jsonPath().getString("data.login.user.id");
        String email = response.jsonPath().getString("data.login.user.email");
        Assert.assertNotNull(userId, "User id should not be null");
        Assert.assertNotNull(email, "Email should not be null");

        // 5. The session cookie was set. Persist it so other tests can send it.
        String sessionCookie = response.getCookie(COOKIE_NAME);
        Assert.assertNotNull(sessionCookie, "Session cookie " + COOKIE_NAME + " should be set");
        Assert.assertFalse(sessionCookie.isEmpty(), "Session cookie should not be empty");

        JSONObject sessionJson = new JSONObject();
        sessionJson.put(COOKIE_NAME, sessionCookie);
        sessionJson.put("userId", userId);
        sessionJson.put("email", email);

        try (FileWriter writer = new FileWriter(SESSION_FILE)) {
            writer.write(sessionJson.toString(4));
            writer.flush();
        }
        System.out.println("Session cookie saved to " + SESSION_FILE);
    }
}
