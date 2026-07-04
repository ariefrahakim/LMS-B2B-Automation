package api.body.graphql;

import org.json.JSONObject;

public class GraphQLPayload {

    public static JSONObject of(String operationName, String query, JSONObject variables) {
        JSONObject body = new JSONObject();
        body.put("operationName", operationName);
        body.put("query", query);
        body.put("variables", variables == null ? new JSONObject() : variables);
        return body;
    }
}
