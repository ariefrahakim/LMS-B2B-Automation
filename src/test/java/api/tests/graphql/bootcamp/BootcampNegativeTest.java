package api.tests.graphql.bootcamp;

import api.base.BaseGraphQLTest;
import api.body.graphql.bootcamp.BootcampBodies;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import api.tests.graphql.NegativeAssertions;

/** Negative scenarios for the Bootcamp (Fast-Track Class) module. */
public class BootcampNegativeTest extends BaseGraphQLTest {

    private static final String NON_EXISTENT_UUID = "00000000-0000-0000-0000-000000000000";

    @Test
    public void getBootcampById_nonExistent() throws Exception {
        Response response = authRequest()
                .body(BootcampBodies.getById(NON_EXISTENT_UUID).toString())
                .when().post("").then().extract().response();
        System.out.println("getBootcampById_nonExistent: " + response.asString());
        NegativeAssertions.assertGraphQLError(response, "data.bootcampById");
    }

    @Test
    public void updateBootcamp_nonExistent() throws Exception {
        Response response = authRequest()
                .body(BootcampBodies.update(NON_EXISTENT_UUID, "x", "d", "2026-07-10", "2026-08-10").toString())
                .when().post("").then().extract().response();
        System.out.println("updateBootcamp_nonExistent: " + response.asString());
        NegativeAssertions.assertGraphQLError(response, "data.updateBootcamp");
    }

    @Test
    public void deleteBootcamp_nonExistent() throws Exception {
        Response response = authRequest()
                .body(BootcampBodies.delete(NON_EXISTENT_UUID).toString())
                .when().post("").then().extract().response();
        System.out.println("deleteBootcamp_nonExistent: " + response.asString());
        NegativeAssertions.assertGraphQLError(response, "data.deleteBootcamp");
    }
}
