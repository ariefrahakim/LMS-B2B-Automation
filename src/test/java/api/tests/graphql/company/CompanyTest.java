package api.tests.graphql.company;

import api.base.BaseGraphQLTest;
import api.body.graphql.company.CompanyBodies;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Company module — positive read queries.
 *
 * The Company entity is exposed as read-only (no create/update/delete for
 * regular admins), so this class only exercises the query side of the module.
 */
public class CompanyTest extends BaseGraphQLTest {

    /** Fetch the current user's company. */
    @Test
    public void getMyCompany() throws Exception {
        Response response = authRequest()
                .body(CompanyBodies.myCompany().toString())
                .when().post("").then().extract().response();

        System.out.println("myCompany: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNull(response.jsonPath().get("errors"));

        Assert.assertNotNull(response.jsonPath().getString("data.myCompany.id"));
        Assert.assertNotNull(response.jsonPath().getString("data.myCompany.name"));
    }

    /** Fetch a company by its public slug. */
    @Test
    public void getCompanyBySlug() throws Exception {
        Response response = authRequest()
                .body(CompanyBodies.companyBySlugFromConfig().toString())
                .when().post("").then().extract().response();

        System.out.println("companyBySlug: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertNull(response.jsonPath().get("errors"));

        Assert.assertNotNull(response.jsonPath().getString("data.companyBySlug.id"));
        Assert.assertNotNull(response.jsonPath().getString("data.companyBySlug.slug"));
    }
}
