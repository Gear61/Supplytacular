import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by alexanderchiou on 10/30/16.
 */
public class AdminService {
    public static final String PATH = "admin";

    public static void getRequests(Connection connection, HttpServletResponse resp, JSONObject requestBody) throws IOException {
        try {
            // Parse request body
            long timeUpdated = requestBody.getLong(Request.TIME_UPDATED_KEY);
            String state = "";
            try {
                state = requestBody.getString(Request.STATE_KEY);
            } catch (JSONException ignored) {}

            String getRequestsQuery = "SELECT * FROM Request WHERE time_updated < ? " +
                    (state.isEmpty() ? "" : "AND state = ? ") +
                    "ORDER BY time_updated DESC;";
            PreparedStatement statement = connection.prepareStatement(getRequestsQuery);
            statement.setLong(1, timeUpdated);
            if (!state.isEmpty()) {
                statement.setString(2, state);
            }

            ResultSet resultSet =  statement.executeQuery();
            JSONArray requests = new JSONArray();
            while (resultSet.next()) {
                JSONObject request = new JSONObject();
                request.put(Constants.ID_KEY, resultSet.getLong(Constants.ID_KEY));
                request.put(Constants.TITLE_KEY, resultSet.getString(Constants.TITLE_KEY));
                request.put(Request.LINK_KEY, resultSet.getString(Request.LINK_KEY));
                request.put(Request.BODY_KEY, resultSet.getString(Request.BODY_KEY));
                request.put(Request.STATE_KEY, resultSet.getString(Request.STATE_KEY));
                request.put(Request.TIME_UPDATED_KEY, resultSet.getLong(Request.TIME_UPDATED_KEY));
                requests.put(request);
            }
            statement.close();
            resp.getWriter().print(requests.toString());
        } catch (JSONException e) {
            resp.setStatus(Constants.BAD_REQUEST);
        } catch (IOException|SQLException exception) {
            resp.setStatus(Constants.INTERNAL_SERVER_ERROR);
            resp.getWriter().print(Utils.getStackTrace(exception));
        }
    }
}
