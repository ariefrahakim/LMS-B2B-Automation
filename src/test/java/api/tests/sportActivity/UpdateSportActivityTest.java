package api.tests.sportActivity;

import api.body.sportActivity.UpdateSportActivityBody;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.ConfigReader;

import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;

public class UpdateSportActivityTest {

    @Test
    public void updateSportActivityFromJson() throws Exception {
        // Set Base URI dari ConfigReader
        RestAssured.baseURI = ConfigReader.getProperty("baseUrl");

        // Ambil token dari file token.json
        String tokenFile = "src/resources/json/token.json";
        String tokenContent = new String(Files.readAllBytes(Paths.get(tokenFile)));
        JSONObject tokenJson = new JSONObject(tokenContent);
        String token = tokenJson.getString("token");

        // Ambil activity_id dari file activity_id.json
        String activityFile = "src/resources/json/activity_id.json";
        String activityContent = new String(Files.readAllBytes(Paths.get(activityFile)));
        JSONObject activityJson = new JSONObject(activityContent);
        int activityId = activityJson.getInt("activity_id");

        // Baca body dari file JSON
        UpdateSportActivityBody bodyHelper = new UpdateSportActivityBody();
        JSONObject requestBody = bodyHelper.getBodyFromFile("src/resources/json/update_activity.json");

        // Kirim request PUT
        Response response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(requestBody.toString())
                .when()
                .post("/sport-activities/update/" + activityId)
                .then()
                .extract().response();

        // Print response
        System.out.println("Response: " + response.asString());
        System.out.println("Activity ID: " + activityId);

        // Validasi
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("message"), "data saved");
        Assert.assertFalse(response.jsonPath().getBoolean("error"));
        }
    }

