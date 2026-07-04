package api.body.graphql.announcement;

import api.body.graphql.GraphQLPayload;
import org.json.JSONObject;

public class AnnouncementBodies {

    public static JSONObject create(String title, String content) {
        String q = "mutation CreateAnnouncement($input: AnnouncementInput!) { " +
                "createAnnouncement(input: $input) { id title content } }";
        JSONObject input = new JSONObject();
        input.put("title", title);
        input.put("content", content);
        input.put("isForAllEmployee", true);
        JSONObject vars = new JSONObject();
        vars.put("input", input);
        return GraphQLPayload.of("CreateAnnouncement", q, vars);
    }

    public static JSONObject getById(String id) {
        String q = "query AnnouncementById($id: String!) { " +
                "announcementById(id: $id) { id title content } }";
        JSONObject vars = new JSONObject();
        vars.put("id", id);
        return GraphQLPayload.of("AnnouncementById", q, vars);
    }

    public static JSONObject update(String id, String title, String content) {
        String q = "mutation UpdateAnnouncement($id: String!, $input: AnnouncementInput!) { " +
                "updateAnnouncement(id: $id, input: $input) { id title content } }";
        JSONObject input = new JSONObject();
        input.put("title", title);
        input.put("content", content);
        input.put("isForAllEmployee", true);
        JSONObject vars = new JSONObject();
        vars.put("id", id);
        vars.put("input", input);
        return GraphQLPayload.of("UpdateAnnouncement", q, vars);
    }

    public static JSONObject delete(String id) {
        String q = "mutation DeleteAnnouncement($id: String!) { deleteAnnouncement(id: $id) }";
        JSONObject vars = new JSONObject();
        vars.put("id", id);
        return GraphQLPayload.of("DeleteAnnouncement", q, vars);
    }
}
