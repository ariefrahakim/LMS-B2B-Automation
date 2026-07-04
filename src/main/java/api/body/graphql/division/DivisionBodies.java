package api.body.graphql.division;

import api.body.graphql.GraphQLPayload;
import org.json.JSONObject;

public class DivisionBodies {

    public static JSONObject create(String name, String description) {
        String q = "mutation CreateDivision($input: DivisionInput!) { " +
                "createDivision(input: $input) { id name description } }";
        JSONObject input = new JSONObject();
        input.put("name", name);
        input.put("description", description);
        JSONObject vars = new JSONObject();
        vars.put("input", input);
        return GraphQLPayload.of("CreateDivision", q, vars);
    }

    public static JSONObject getById(String id) {
        String q = "query DivisionById($id: String!) { " +
                "divisionById(id: $id) { id name description } }";
        JSONObject vars = new JSONObject();
        vars.put("id", id);
        return GraphQLPayload.of("DivisionById", q, vars);
    }

    public static JSONObject update(String id, String name, String description) {
        String q = "mutation UpdateDivision($id: String!, $input: DivisionInput!) { " +
                "updateDivision(id: $id, input: $input) { id name description } }";
        JSONObject input = new JSONObject();
        input.put("name", name);
        input.put("description", description);
        JSONObject vars = new JSONObject();
        vars.put("id", id);
        vars.put("input", input);
        return GraphQLPayload.of("UpdateDivision", q, vars);
    }

    public static JSONObject delete(String id) {
        String q = "mutation DeleteDivision($id: String!) { deleteDivision(id: $id) }";
        JSONObject vars = new JSONObject();
        vars.put("id", id);
        return GraphQLPayload.of("DeleteDivision", q, vars);
    }
}
