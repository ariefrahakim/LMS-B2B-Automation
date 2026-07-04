package api.tests.graphql.auth;

import api.base.BaseGraphQLTest;
import api.body.graphql.auth.AuthBodies;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.ConfigReader;

/**
 * Negative scenarios for the Auth / Session module.
 *
 * Every scenario expects HTTP 200 (GraphQL convention) with either a business
 * error in the `login.errors` field OR a top-level `errors` array.
 */
public class AuthNegativeTest extends BaseGraphQLTest {

    /** Login with a valid user email but the wrong password. */
    @Test
    public void loginWithWrongPassword() {
        RestAssured.baseURI = ConfigReader.getProperty("url");
        Response response = baseRequest()
                .body(AuthBodies.login(
                        ConfigReader.getProperty("emailWeb"),
                        "wrong-password",
                        ConfigReader.getProperty("companyId")).toString())
                .when().post("")
                .then().extract().response();

        System.out.println("loginWithWrongPassword: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);

        JSONObject json = new JSONObject(response.asString());
        boolean userIsNull = json.optJSONObject("data") != null
                && json.optJSONObject("data").optJSONObject("login") != null
                && json.optJSONObject("data").optJSONObject("login").isNull("user");
        boolean hasErrors = json.optJSONArray("errors") != null;
        Assert.assertTrue(userIsNull || hasErrors,
                "Wrong password must result in null user OR errors");
    }

    /** Login using a random / non-existent companyId. */
    @Test
    public void loginWithWrongCompanyId() {
        RestAssured.baseURI = ConfigReader.getProperty("url");
        Response response = baseRequest()
                .body(AuthBodies.login(
                        ConfigReader.getProperty("emailWeb"),
                        ConfigReader.getProperty("passwordWeb"),
                        "00000000-0000-0000-0000-000000000000").toString())
                .when().post("")
                .then().extract().response();

        System.out.println("loginWithWrongCompanyId: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);

        JSONObject json = new JSONObject(response.asString());
        boolean userIsNull = json.optJSONObject("data") != null
                && json.optJSONObject("data").optJSONObject("login") != null
                && json.optJSONObject("data").optJSONObject("login").isNull("user");
        boolean hasErrors = json.optJSONArray("errors") != null;
        Assert.assertTrue(userIsNull || hasErrors,
                "Wrong companyId must result in null user OR errors");
    }

    /** Login with a syntactically invalid email address. */
    @Test
    public void loginWithMalformedEmail() {
        RestAssured.baseURI = ConfigReader.getProperty("url");
        Response response = baseRequest()
                .body(AuthBodies.login("not-an-email",
                        ConfigReader.getProperty("passwordWeb"),
                        ConfigReader.getProperty("companyId")).toString())
                .when().post("")
                .then().extract().response();
        System.out.println("loginWithMalformedEmail: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);

        JSONObject json = new JSONObject(response.asString());
        boolean userIsNull = json.optJSONObject("data") != null
                && json.optJSONObject("data").optJSONObject("login") != null
                && json.optJSONObject("data").optJSONObject("login").isNull("user");
        boolean hasErrors = json.optJSONArray("errors") != null;
        Assert.assertTrue(userIsNull || hasErrors,
                "Malformed email must not authenticate");
    }

    /** `me` query without the session cookie must return "not authenticated". */
    @Test
    public void queryMeWithoutSessionCookie() {
        RestAssured.baseURI = ConfigReader.getProperty("url");
        Response response = baseRequest()
                .body(AuthBodies.me().toString())
                .when().post("")
                .then().extract().response();

        System.out.println("queryMeWithoutSessionCookie: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNotNull(response.jsonPath().get("errors"),
                "Calling `me` without session cookie must return errors");
        Assert.assertTrue(response.asString().contains("not authenticated"),
                "Error message should say 'not authenticated'");
    }
}
