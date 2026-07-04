package api.body.graphql.program;

import api.body.graphql.GraphQLPayload;
import org.json.JSONObject;

public class ProgramBodies {

    public static JSONObject create(String title, String description, String type) {
        String q = "mutation CreateProgram($input: ProgramInput!) { " +
                "createProgram(input: $input) { id title description type isSequential } }";
        JSONObject input = new JSONObject();
        input.put("title", title);
        input.put("description", description);
        input.put("type", type);
        input.put("isSequential", false);
        JSONObject vars = new JSONObject();
        vars.put("input", input);
        return GraphQLPayload.of("CreateProgram", q, vars);
    }

    public static JSONObject getById(String id) {
        String q = "query ProgramById($id: String!) { " +
                "programById(id: $id) { id title description type } }";
        JSONObject vars = new JSONObject();
        vars.put("id", id);
        return GraphQLPayload.of("ProgramById", q, vars);
    }

    public static JSONObject update(String id, String title, String description, String type) {
        String q = "mutation UpdateProgram($id: String!, $input: ProgramInput!) { " +
                "updateProgram(id: $id, input: $input) { id title description type } }";
        JSONObject input = new JSONObject();
        input.put("title", title);
        input.put("description", description);
        input.put("type", type);
        JSONObject vars = new JSONObject();
        vars.put("id", id);
        vars.put("input", input);
        return GraphQLPayload.of("UpdateProgram", q, vars);
    }

    public static JSONObject delete(String id) {
        String q = "mutation DeleteProgram($id: String!) { deleteProgram(id: $id) }";
        JSONObject vars = new JSONObject();
        vars.put("id", id);
        return GraphQLPayload.of("DeleteProgram", q, vars);
    }
}
