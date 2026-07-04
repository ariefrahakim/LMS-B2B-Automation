package api.base;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.annotations.BeforeClass;
import utils.ConfigReader;

import java.io.File;
import java.io.FileReader;

import static io.restassured.RestAssured.given;

public class BaseGraphQLTest {

    protected static final String SESSION_FILE = "src/resources/json/graphql_session.json";
    protected static final String COOKIE_NAME = "sid_b2b";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = ConfigReader.getProperty("url");
    }

    protected RequestSpecification baseRequest() {
        return given()
                .auth().preemptive().basic(
                        ConfigReader.getProperty("usernameGraphQL"),
                        ConfigReader.getProperty("passwordGraphQL"))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");
    }

    protected RequestSpecification authRequest() throws Exception {
        return baseRequest().cookie(COOKIE_NAME, readSessionCookie());
    }

    protected String readSessionCookie() throws Exception {
        File f = new File(SESSION_FILE);
        if (!f.exists()) {
            throw new IllegalStateException("Session file not found. Run LoginGraphQLTest first: " + SESSION_FILE);
        }
        try (FileReader reader = new FileReader(f)) {
            JSONObject json = new JSONObject(new JSONTokener(reader));
            return json.getString(COOKIE_NAME);
        }
    }
}
