package api.body.graphql.company;

import api.body.graphql.GraphQLPayload;
import org.json.JSONObject;
import utils.ConfigReader;

/**
 * Company-module GraphQL bodies:
 *   - `myCompany`     — the company the logged-in user belongs to.
 *   - `companyBySlug` — public lookup by slug (e.g. "dibimbingqa").
 */
public class CompanyBodies {

    public static JSONObject myCompany() {
        return GraphQLPayload.of("MyCompany",
                "query MyCompany { myCompany { id name slug } }",
                new JSONObject());
    }

    public static JSONObject companyBySlug(String slug) {
        String q = "query CompanyBySlug($slug: String!) { " +
                "companyBySlug(slug: $slug) { id name slug status } }";
        JSONObject vars = new JSONObject();
        vars.put("slug", slug);
        return GraphQLPayload.of("CompanyBySlug", q, vars);
    }

    /** Convenience — use the slug from config.properties (falling back to `dibimbingqa`). */
    public static JSONObject companyBySlugFromConfig() {
        String slug = ConfigReader.getProperty("companySlug");
        if (slug == null || slug.isEmpty() || "companySlug".equals(slug)) {
            slug = "dibimbingqa";
        }
        return companyBySlug(slug);
    }
}
