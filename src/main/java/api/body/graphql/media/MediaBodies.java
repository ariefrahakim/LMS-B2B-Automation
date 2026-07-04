package api.body.graphql.media;

import api.body.graphql.GraphQLPayload;
import org.json.JSONObject;

public class MediaBodies {

    public static JSONObject medias() {
        String q = "query Medias { medias { id title extension size type mimeType url status } }";
        return GraphQLPayload.of("Medias", q, new JSONObject());
    }

    public static JSONObject countMedias() {
        String q = "query CountMedias { countMedias }";
        return GraphQLPayload.of("CountMedias", q, new JSONObject());
    }

    public static JSONObject availableStorage() {
        String q = "query AvailableStorage { availableStorage { max occupied available } }";
        return GraphQLPayload.of("AvailableStorage", q, new JSONObject());
    }

    public static JSONObject mediaById(String id) {
        String q = "query MediaById($id: String!) { mediaById(id: $id) { id title extension size type mimeType url status } }";
        JSONObject vars = new JSONObject();
        vars.put("id", id);
        return GraphQLPayload.of("MediaById", q, vars);
    }
}
