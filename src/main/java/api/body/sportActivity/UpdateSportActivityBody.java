package api.body.sportActivity;

import org.json.JSONObject;
import org.json.JSONTokener;
import utils.Utils;

import java.io.FileInputStream;

public class UpdateSportActivityBody {

    public JSONObject getBodyFromFile(String filePath) throws Exception {
        FileInputStream file = new FileInputStream(filePath);
        JSONObject body = new JSONObject(new JSONTokener(file));
        body.put("activity_date", Utils.getDateAfterFourDays());
        file.close();
        return body;
    }
}
