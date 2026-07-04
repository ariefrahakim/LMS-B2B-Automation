package api.tests.graphql.company;

import api.base.BaseGraphQLTest;
import api.body.graphql.company.CompanyBodies;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Negative scenarios for the Company module.
 */
public class CompanyNegativeTest extends BaseGraphQLTest {

    /** Look up a company by a slug that does not exist — expect null result. */
    @Test
    public void companyBySlug_nonExistent() throws Exception {
        Response response = authRequest()
                .body(CompanyBodies.companyBySlug("this-slug-does-not-exist-xyz").toString())
                .when().post("").then().extract().response();

        System.out.println("companyBySlug_nonExistent: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        boolean nullResult = response.jsonPath().get("data.companyBySlug") == null;
        boolean hasErrors = response.jsonPath().get("errors") != null;
        Assert.assertTrue(nullResult || hasErrors,
                "A non-existent slug must return null result or an errors payload");
    }
}
