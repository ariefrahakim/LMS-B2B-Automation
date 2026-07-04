package api.tests.graphql.angkatan;

import api.base.BaseGraphQLTest;
import api.body.graphql.angkatan.AngkatanBodies;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileReader;

/**
 * Data-driven positive test — creates one Angkatan per row in
 * {@code src/resources/json/angkatan_data.json}. Every row is asserted
 * individually and cleaned up (deleted) at the end of the same iteration to
 * keep the test idempotent.
 *
 * Adding a new test case does not require any Java changes: just add a new
 * entry to the JSON file.
 */
public class AngkatanDataDrivenTest extends BaseGraphQLTest {

    private static final String DATA_FILE = "src/resources/json/angkatan_data.json";

    /** TestNG @DataProvider — parses the JSON fixture into a {name, description} matrix. */
    @DataProvider(name = "angkatanData")
    public Object[][] angkatanData() throws Exception {
        try (FileReader reader = new FileReader(DATA_FILE)) {
            JSONObject json = new JSONObject(new JSONTokener(reader));
            JSONArray arr = json.getJSONArray("angkatans");
            Object[][] data = new Object[arr.length()][2];
            for (int i = 0; i < arr.length(); i++) {
                JSONObject row = arr.getJSONObject(i);
                data[i][0] = row.getString("name");
                data[i][1] = row.optString("description", "");
            }
            return data;
        }
    }

    @Test(dataProvider = "angkatanData")
    public void createAngkatanFromJson(String name, String description) throws Exception {
        // 1. Create
        Response response = authRequest()
                .body(AngkatanBodies.create(name, description).toString())
                .when().post("").then().extract().response();

        System.out.println("Response for [" + name + "]: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNull(response.jsonPath().get("errors"));

        Number id = response.jsonPath().get("data.createAngkatan.id");
        Assert.assertNotNull(id);
        Assert.assertEquals(response.jsonPath().getString("data.createAngkatan.name"), name);
        Assert.assertEquals(response.jsonPath().getString("data.createAngkatan.description"), description);

        // 2. Cleanup — delete right away so re-running the suite is safe.
        Response del = authRequest()
                .body(AngkatanBodies.delete(id).toString())
                .when().post("").then().extract().response();
        Assert.assertEquals(del.getStatusCode(), 200, "Cleanup delete status should be 200");
        Assert.assertNull(del.jsonPath().get("errors"), "Cleanup delete should have no errors");
    }
}
