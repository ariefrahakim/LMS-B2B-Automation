package api.tests.sportActivity;

import api.body.sportActivity.CreateSportActivityBody;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.ConfigReader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static io.restassured.RestAssured.given;

public class CreateSportActivityTest {

    private String token;

    @BeforeClass
    public void setup() throws Exception {
        // Set base URI
        RestAssured.baseURI = ConfigReader.getProperty("baseUrl");

        // Baca token dari file token.json
        FileReader reader = new FileReader("src/resources/json/token.json");
        JSONObject tokenJson = new JSONObject(new org.json.JSONTokener(reader));
        token = tokenJson.getString("token");
        reader.close();
    }

    @Test
    public void createSportActivity() throws IOException {
        // Buat body dari class
        CreateSportActivityBody bodyObj = new CreateSportActivityBody();
        JSONObject requestBody = bodyObj.getBody();

        // Kirim request POST
        Response response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(requestBody.toString())
                .when()
                .post("/sport-activities/create")
                .then()
                .extract().response();

        System.out.println("Response: " + response.asString());

        // Validasi status code
        Assert.assertEquals(response.getStatusCode(), 200);

        // Validasi error = false
        Assert.assertFalse(response.jsonPath().getBoolean("error"));

        // Validasi message
        Assert.assertEquals(response.jsonPath().getString("message"), "data saved");

        // Save activity id
        String activity_id = response.jsonPath().getString("result.id");
        System.out.println("Activity ID: " + activity_id);

        JSONObject tokenJson = new JSONObject();
        tokenJson.put("activity_id", activity_id);

        try (FileWriter file = new FileWriter("src/resources/json/activity_id.json")) {
            file.write(tokenJson.toString(4)); // 4 = indentation
            file.flush();
        }
    }
}
