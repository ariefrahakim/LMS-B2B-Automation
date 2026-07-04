package api.body.auth;

import org.json.JSONObject;
import utils.ConfigReader;

public class LoginBody {

    public JSONObject loginData() {
        JSONObject body = new JSONObject();
        body.put("email", ConfigReader.getProperty("email"));
        body.put("password", ConfigReader.getProperty("password"));
        return body;
    }
}
