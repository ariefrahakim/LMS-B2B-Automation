package api.tests.sportActivity;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.ConfigReader;

import java.io.FileReader;
import java.io.IOException;

import static io.restassured.RestAssured.given;

public class GetSportActivityByIdTest {

    private String token;

    @BeforeClass
    public void setup() throws Exception {
        // Set base URI dari config.properties
        RestAssured.baseURI = ConfigReader.getProperty("baseUrl");

        // Baca token dari file src/test/resources/json/token.json
        FileReader reader = new FileReader("src/resources/json/token.json");
        JSONObject tokenJson = new JSONObject(new org.json.JSONTokener(reader));
        token = tokenJson.getString("token");
        reader.close();

        System.out.println("Token loaded: " + token);
    }

    @Test
    public void GetSportActivityById() throws IOException {
        // Baca file JSON
        FileReader reader = new FileReader("src/resources/json/activity_id.json");
        JSONObject json = new JSONObject(new JSONTokener(reader));
        reader.close();

        // Ambil activity_id dari JSON
        int activityId = json.getInt("activity_id");

        System.out.println("Activity ID: " + activityId);

        Response response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .when()
                .get("/sport-activities/" + activityId)
                .then()
                .extract().response();

        // Print response
        System.out.println("Response: " + response.asString());

        // Validasi status code
        Assert.assertEquals(response.getStatusCode(), 200);

        // Validasi error = false
        Boolean error = response.jsonPath().getBoolean("error");
        Assert.assertFalse(error, "Expected error = false");

        // Validasi result.id sesuai
        Integer id = response.jsonPath().getInt("result.id");
        Assert.assertEquals(id.intValue(), activityId, "Activity ID tidak sesuai");

        String description = response.jsonPath().getString("result.description");
        Assert.assertNotNull(description, "Description should not be null");

    }
}
