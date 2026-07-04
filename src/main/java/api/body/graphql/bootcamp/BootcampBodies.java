package api.body.graphql.bootcamp;

import api.body.graphql.GraphQLPayload;
import org.json.JSONObject;

public class BootcampBodies {

    public static JSONObject create(String title, String description, String startedAt, String finishedAt) {
        String q = "mutation CreateBootcamp($input: InputBootcamp!) { " +
                "createBootcamp(input: $input) { id title descriptions startedAt finishedAt } }";
        JSONObject input = new JSONObject();
        input.put("title", title);
        input.put("descriptions", description);
        input.put("startedAt", startedAt);
        input.put("finishedAt", finishedAt);
        JSONObject vars = new JSONObject();
        vars.put("input", input);
        return GraphQLPayload.of("CreateBootcamp", q, vars);
    }

    public static JSONObject getById(String id) {
        String q = "query BootcampById($id: String!) { " +
                "bootcampById(id: $id) { id title descriptions startedAt finishedAt } }";
        JSONObject vars = new JSONObject();
        vars.put("id", id);
        return GraphQLPayload.of("BootcampById", q, vars);
    }

    public static JSONObject update(String id, String title, String description, String startedAt, String finishedAt) {
        String q = "mutation UpdateBootcamp($id: String!, $input: InputBootcamp!) { " +
                "updateBootcamp(id: $id, input: $input) { id title descriptions startedAt finishedAt } }";
        JSONObject input = new JSONObject();
        input.put("title", title);
        input.put("descriptions", description);
        input.put("startedAt", startedAt);
        input.put("finishedAt", finishedAt);
        JSONObject vars = new JSONObject();
        vars.put("id", id);
        vars.put("input", input);
        return GraphQLPayload.of("UpdateBootcamp", q, vars);
    }

    public static JSONObject delete(String id) {
        // deleteBootcamp returns a Bootcamp object (not Boolean), so we must supply a selection set.
        String q = "mutation DeleteBootcamp($id: String!) { deleteBootcamp(id: $id) { id title } }";
        JSONObject vars = new JSONObject();
        vars.put("id", id);
        return GraphQLPayload.of("DeleteBootcamp", q, vars);
    }
}
