package api.tests.auth;

import api.body.auth.LoginBody;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.ConfigReader;

import java.io.FileWriter;
import java.io.IOException;

import static io.restassured.RestAssured.given;

public class LoginTest {

    @Test
    public void login() throws IOException {
        // Set base URI dari ConfigReader
        RestAssured.baseURI = ConfigReader.getProperty("baseUrl");

        // Buat body login
        LoginBody loginBody = new LoginBody();

        // Kirim request POST ke endpoint login
        Response response = given()
                .header("Content-Type", "application/json")
                .body(loginBody.loginData().toString())
                .when()
                .post("/login") // endpoint
                .then()
                .extract().response();

        // Print response
        System.out.println("Response: " + response.asString());

        // Assert status code 200
        Assert.assertEquals(response.getStatusCode(), 200);

        // Validasi token
        String token = response.jsonPath().getString("data.token");
        Assert.assertNotNull(token, "Token should not be null");
        Assert.assertFalse(token.isEmpty(), "Token should not be empty");
        System.out.println("Token: " + token);

        // Validasi message
        String message = response.jsonPath().getString("message");
        Assert.assertEquals(message, "User login successfully.", "Message does not match");

        // Simpan token ke file resources/json/token.json
        JSONObject tokenJson = new JSONObject();
        tokenJson.put("token", token);

        try (FileWriter file = new FileWriter("src/resources/json/token.json")) {
            file.write(tokenJson.toString(4)); // 4 = indentation
            file.flush();
        }

        System.out.println("Token berhasil disimpan di resources/json/token.json");
    }
}
