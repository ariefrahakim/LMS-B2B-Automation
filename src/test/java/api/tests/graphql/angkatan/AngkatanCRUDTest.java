package api.tests.graphql.angkatan;

import api.base.BaseGraphQLTest;
import api.body.graphql.angkatan.AngkatanBodies;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.Utils;

import java.util.List;

/**
 * Positive CRUD flow for the "Angkatan" (cohort) module.
 *
 * Sequence: create → list → get by id → update → delete. State is threaded
 * through {@code static} fields because TestNG runs the @Test methods of a
 * class on a single thread; ordering is enforced with {@code priority}.
 */
public class AngkatanCRUDTest extends BaseGraphQLTest {

    private static Number angkatanId;
    private static String createdName;

    // Step 1 — create a new angkatan and remember its id for the rest of the flow.
    @Test(priority = 1)
    public void createAngkatan() throws Exception {
        createdName = "QA-CRUD-" + Utils.generateRandomTitle();
        Response response = authRequest()
                .body(AngkatanBodies.create(createdName, "CRUD flow angkatan").toString())
                .when().post("").then().extract().response();

        System.out.println("createAngkatan: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNull(response.jsonPath().get("errors"));

        angkatanId = response.jsonPath().get("data.createAngkatan.id");
        Assert.assertNotNull(angkatanId);
        Assert.assertEquals(response.jsonPath().getString("data.createAngkatan.name"), createdName);
    }

    // Step 2 — list all angkatans and check that our just-created id shows up.
    @Test(priority = 2, dependsOnMethods = "createAngkatan")
    public void listAngkatans() throws Exception {
        Response response = authRequest()
                .body(AngkatanBodies.list().toString())
                .when().post("").then().extract().response();

        System.out.println("angkatans: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNull(response.jsonPath().get("errors"));

        List<Number> ids = response.jsonPath().getList("data.angkatans.id");
        Assert.assertTrue(ids.stream().anyMatch(i -> i.intValue() == angkatanId.intValue()),
                "Newly created angkatan should appear in the list");
    }

    // Step 3 — fetch the angkatan by id and verify the payload.
    @Test(priority = 3, dependsOnMethods = "createAngkatan")
    public void getAngkatanById() throws Exception {
        Response response = authRequest()
                .body(AngkatanBodies.byId(angkatanId).toString())
                .when().post("").then().extract().response();

        System.out.println("angkatanById: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNull(response.jsonPath().get("errors"));
        Assert.assertEquals(((Number) response.jsonPath().get("data.angkatanById.id")).intValue(),
                angkatanId.intValue());
    }

    // Step 4 — update the angkatan and confirm the change is persisted (via a re-fetch).
    @Test(priority = 4, dependsOnMethods = "createAngkatan")
    public void updateAngkatan() throws Exception {
        String newName = "QA-CRUD-Updated-" + Utils.generateRandomTitle();
        Response response = authRequest()
                .body(AngkatanBodies.update(angkatanId, newName, "updated").toString())
                .when().post("").then().extract().response();

        System.out.println("updateAngkatan: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNull(response.jsonPath().get("errors"));

        Response reload = authRequest()
                .body(AngkatanBodies.byId(angkatanId).toString())
                .when().post("").then().extract().response();
        Assert.assertEquals(reload.jsonPath().getString("data.angkatanById.name"), newName,
                "Persisted name must match the update");
    }

    // Step 5 — delete the angkatan and expect a `true` acknowledgement.
    @Test(priority = 5, dependsOnMethods = "createAngkatan")
    public void deleteAngkatan() throws Exception {
        Response response = authRequest()
                .body(AngkatanBodies.delete(angkatanId).toString())
                .when().post("").then().extract().response();

        System.out.println("deleteAngkatan: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNull(response.jsonPath().get("errors"));
        Assert.assertTrue(response.jsonPath().getBoolean("data.deleteAngkatan"));
    }
}
