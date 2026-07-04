package api.body.graphql.employee;

import api.body.graphql.GraphQLPayload;
import org.json.JSONObject;

public class EmployeeBodies {

    public static JSONObject create(String name, String email, String role) {
        String q = "mutation CreateEmployee($input: EmployeeInput!) { " +
                "createEmployee(input: $input) { id email name employeeRole status } }";
        JSONObject input = new JSONObject();
        input.put("name", name);
        input.put("email", email);
        input.put("employeeRole", role);
        JSONObject vars = new JSONObject();
        vars.put("input", input);
        return GraphQLPayload.of("CreateEmployee", q, vars);
    }

    public static JSONObject getById(String id) {
        String q = "query EmployeeById($id: String!) { " +
                "employeeById(id: $id) { id email name employeeRole status } }";
        JSONObject vars = new JSONObject();
        vars.put("id", id);
        return GraphQLPayload.of("EmployeeById", q, vars);
    }

    public static JSONObject update(String id, String newName, String email, String role) {
        String q = "mutation UpdateEmployee($id: String!, $input: EmployeeInput!) { " +
                "updateEmployee(id: $id, input: $input) { id name email employeeRole } }";
        JSONObject input = new JSONObject();
        input.put("name", newName);
        input.put("email", email);
        input.put("employeeRole", role);
        JSONObject vars = new JSONObject();
        vars.put("id", id);
        vars.put("input", input);
        return GraphQLPayload.of("UpdateEmployee", q, vars);
    }

    public static JSONObject delete(String id) {
        String q = "mutation DeleteEmployee($id: String!) { deleteEmployee(id: $id) }";
        JSONObject vars = new JSONObject();
        vars.put("id", id);
        return GraphQLPayload.of("DeleteEmployee", q, vars);
    }
}
