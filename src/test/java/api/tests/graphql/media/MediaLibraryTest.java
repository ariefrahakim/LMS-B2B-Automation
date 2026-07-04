package api.tests.graphql.media;

import api.base.BaseGraphQLTest;
import api.body.graphql.media.MediaBodies;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class MediaLibraryTest extends BaseGraphQLTest {

    private static String firstMediaId;

    @Test(priority = 1)
    public void listMedias() throws Exception {
        Response response = authRequest()
                .body(MediaBodies.medias().toString())
                .when().post("")
                .then().extract().response();

        System.out.println("medias: " + response.asString().substring(0, Math.min(300, response.asString().length())));
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNull(response.jsonPath().get("errors"));

        List<Object> list = response.jsonPath().getList("data.medias");
        Assert.assertNotNull(list, "medias list should not be null");
        if (!list.isEmpty()) {
            firstMediaId = response.jsonPath().getString("data.medias[0].id");
        }
    }

    @Test(priority = 2)
    public void countMedias() throws Exception {
        Response response = authRequest()
                .body(MediaBodies.countMedias().toString())
                .when().post("")
                .then().extract().response();

        System.out.println("countMedias: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNull(response.jsonPath().get("errors"));
        Assert.assertNotNull(response.jsonPath().get("data.countMedias"));
    }

    @Test(priority = 3)
    public void availableStorage() throws Exception {
        Response response = authRequest()
                .body(MediaBodies.availableStorage().toString())
                .when().post("")
                .then().extract().response();

        System.out.println("availableStorage: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNull(response.jsonPath().get("errors"));
        Assert.assertNotNull(response.jsonPath().get("data.availableStorage.max"));
    }

    @Test(priority = 4, dependsOnMethods = "listMedias")
    public void mediaById() throws Exception {
        if (firstMediaId == null) {
            throw new org.testng.SkipException("No media available to fetch by id");
        }
        Response response = authRequest()
                .body(MediaBodies.mediaById(firstMediaId).toString())
                .when().post("")
                .then().extract().response();

        System.out.println("mediaById: " + response.asString().substring(0, Math.min(300, response.asString().length())));
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNull(response.jsonPath().get("errors"));
        Assert.assertEquals(response.jsonPath().getString("data.mediaById.id"), firstMediaId);
    }
}
