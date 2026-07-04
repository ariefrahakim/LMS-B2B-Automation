package api.body.graphql.angkatan;

import api.body.graphql.GraphQLPayload;
import org.json.JSONObject;

/**
 * Angkatan (cohort) GraphQL bodies for the full CRUD flow + list query.
 *
 * The id argument of every Angkatan resolver is typed as `Float!` on the server
 * (a schema quirk), so we accept a {@link Number} here for callers to pass an int.
 */
public class AngkatanBodies {

    public static JSONObject list() {
        return GraphQLPayload.of("Angkatans",
                "query Angkatans { angkatans { id name } }",
                new JSONObject());
    }

    public static JSONObject byId(Number id) {
        String q = "query AngkatanById($id: Float!) { angkatanById(id: $id) { id name description } }";
        JSONObject vars = new JSONObject();
        vars.put("id", id);
        return GraphQLPayload.of("AngkatanById", q, vars);
    }

    public static JSONObject create(String name, String description) {
        String q = "mutation CreateAngkatan($input: AngkatanInput!) { " +
                "createAngkatan(input: $input) { id name description } }";
        JSONObject input = new JSONObject();
        input.put("name", name);
        input.put("description", description);
        JSONObject vars = new JSONObject();
        vars.put("input", input);
        return GraphQLPayload.of("CreateAngkatan", q, vars);
    }

    public static JSONObject update(Number id, String name, String description) {
        String q = "mutation UpdateAngkatan($id: Float!, $input: AngkatanInput!) { " +
                "updateAngkatan(id: $id, input: $input) { id name description } }";
        JSONObject input = new JSONObject();
        input.put("name", name);
        input.put("description", description);
        JSONObject vars = new JSONObject();
        vars.put("id", id);
        vars.put("input", input);
        return GraphQLPayload.of("UpdateAngkatan", q, vars);
    }

    public static JSONObject delete(Number id) {
        String q = "mutation DeleteAngkatan($id: Float!) { deleteAngkatan(id: $id) }";
        JSONObject vars = new JSONObject();
        vars.put("id", id);
        return GraphQLPayload.of("DeleteAngkatan", q, vars);
    }
}
