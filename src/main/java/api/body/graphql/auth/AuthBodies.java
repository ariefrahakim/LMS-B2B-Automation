package api.body.graphql.auth;

import api.body.graphql.GraphQLPayload;
import org.json.JSONObject;
import utils.ConfigReader;

/**
 * All GraphQL request bodies that belong to the Auth / Session module:
 *   - `login`   mutation (used to obtain a session cookie)
 *   - `me`      query    (identity of the currently-authenticated user)
 *   - `ping`    query    (unauthenticated smoke test)
 *
 * Consolidated in a single class so callers can reference every auth payload
 * from one import — mirrors the module-per-package layout used elsewhere.
 */
public class AuthBodies {

    /** Log in with the credentials stored in config.properties. */
    public static JSONObject login() {
        return login(
                ConfigReader.getProperty("emailWeb"),
                ConfigReader.getProperty("passwordWeb"),
                ConfigReader.getProperty("companyId"));
    }

    /** Log in with explicit credentials (used by negative tests). */
    public static JSONObject login(String usernameOrEmail, String password, String companyId) {
        String q = "mutation Login($usernameOrEmail: String!, $password: String!, $companyId: String!) { " +
                "login(usernameOrEmail: $usernameOrEmail, password: $password, companyId: $companyId) { " +
                "user { id email name role } errors { field message } } }";
        JSONObject vars = new JSONObject();
        vars.put("usernameOrEmail", usernameOrEmail);
        vars.put("password", password);
        vars.put("companyId", companyId);
        return GraphQLPayload.of("Login", q, vars);
    }

    /** `me` query — returns the currently-authenticated user. */
    public static JSONObject me() {
        String q = "query Me { me { id email name role status username phoneNumber companyId } }";
        return GraphQLPayload.of("Me", q, new JSONObject());
    }

    /** Unauthenticated liveness probe. Returns the string "pong". */
    public static JSONObject ping() {
        return GraphQLPayload.of("Ping", "query Ping { ping }", new JSONObject());
    }
}
