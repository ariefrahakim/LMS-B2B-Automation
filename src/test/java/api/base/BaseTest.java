package api.base;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;
import utils.ConfigReader;

public class BaseTest {
    @BeforeClass
    public void setup() {
        String baseUrl = ConfigReader.getProperty("baseUrl");
        RestAssured.baseURI = baseUrl;
    }
}
