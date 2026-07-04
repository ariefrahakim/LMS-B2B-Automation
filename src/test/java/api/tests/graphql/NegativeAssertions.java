package api.tests.graphql;

import io.restassured.response.Response;
import org.testng.Assert;

/**
 * Shared assertion helpers for negative tests across every module.
 *
 * GraphQL error signalling isn't uniform across this server: some errors surface
 * as an `errors` array on HTTP 200, some as HTTP 400 with a validation payload,
 * and some as `data.<field>` being null. This helper accepts any of the three.
 */
public final class NegativeAssertions {

    private NegativeAssertions() {}

    /**
     * Assert that the given response represents a failed (negative-path) call.
     *
     * @param response the HTTP response.
     * @param dataPath dot-path where a null value should be reported if the
     *                 server returns HTTP 200 without an `errors` array.
     */
    public static void assertGraphQLError(Response response, String dataPath) {
        int status = response.getStatusCode();
        Assert.assertTrue(status == 200 || status == 400,
                "Negative request should return 200 (errors) or 400 (validation), got " + status);
        if (status == 400) return; // Transport-level rejection is enough.

        boolean hasErrors = response.jsonPath().get("errors") != null;
        boolean dataNull = response.jsonPath().get(dataPath) == null;
        Assert.assertTrue(hasErrors || dataNull,
                "Expected `errors` array or null data at " + dataPath + " — got: " + response.asString());
    }
}
