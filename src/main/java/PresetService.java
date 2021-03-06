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
 * Created by alexanderchiou on 7/27/16.
 */
public class PresetService {
    public static final String PATH = "presets";
    private static final String PURCHASE_LINK_KEY = "purchase_link";
    private static final String IMAGE_LINK_KEY = "image_link";

    public static void getRequests(Connection connection, HttpServletResponse resp) throws IOException {
        try {
            String getPresetsQuery = "SELECT * FROM Preset;";
            PreparedStatement statement = connection.prepareStatement(getPresetsQuery);

            ResultSet resultSet =  statement.executeQuery();
            JSONArray presets = new JSONArray();
            while (resultSet.next()) {
                JSONObject request = new JSONObject();
                request.put(Constants.ID_KEY, resultSet.getLong(Constants.ID_KEY));
                request.put(Constants.TITLE_KEY, resultSet.getString(Constants.TITLE_KEY));
                request.put(PURCHASE_LINK_KEY, resultSet.getString(PURCHASE_LINK_KEY));
                request.put(IMAGE_LINK_KEY, resultSet.getString(IMAGE_LINK_KEY));
                presets.put(request);
            }
            statement.close();
            resp.getWriter().print(presets.toString());
        } catch (JSONException e) {
            resp.setStatus(Constants.BAD_REQUEST);
        } catch (IOException|SQLException exception) {
            resp.setStatus(Constants.INTERNAL_SERVER_ERROR);
            resp.getWriter().print(Utils.getStackTrace(exception));
        }
    }

    public static void addPreset(Connection connection, HttpServletResponse resp, JSONObject request) throws IOException {
        try {
            // Parse request body
            String title = request.getString(Constants.TITLE_KEY);
            String purchaseLink = request.getString(PURCHASE_LINK_KEY);
            String imageLink = request.getString(IMAGE_LINK_KEY);

            // Insert user
            String insertQuery = "INSERT INTO Preset (title, purchase_link, image_link) " +
                    "VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(insertQuery);
            statement.setString(1, title);
            statement.setString(2, purchaseLink);
            statement.setString(3, imageLink);

            statement.executeUpdate();
            statement.close();
        } catch (JSONException e) {
            resp.setStatus(Constants.BAD_REQUEST);
            resp.getWriter().print(e.getMessage());
        } catch (SQLException exception) {
            resp.setStatus(Constants.INTERNAL_SERVER_ERROR);
            resp.getWriter().print(Utils.getStackTrace(exception));
        }
    }

    public static void editPreset(Connection connection, HttpServletResponse resp, JSONObject requestBody) throws IOException {
        try {
            // Parse request body
            long presetId;
            try {
                presetId = requestBody.getLong(Constants.ID_KEY);
            } catch (JSONException e) {
                resp.setStatus(Constants.BAD_REQUEST);
                resp.getWriter().print(Utils.getStackTrace(e));
                return;
            }
            Preset preset = getPreset(connection, resp, presetId);
            try {
                preset.setTitle(requestBody.getString(Constants.TITLE_KEY));
            } catch (JSONException ignored) {}
            try {
                preset.setPurchaseLink(requestBody.getString(PURCHASE_LINK_KEY));
            } catch (JSONException ignored) {}
            try {
                preset.setImageLink(requestBody.getString(IMAGE_LINK_KEY));
            } catch (JSONException ignored) {}

            // Update request
            String updateQuery = "UPDATE Preset " +
                    "SET title = ?, purchase_link = ?, image_link = ? " +
                    "WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(updateQuery);
            statement.setString(1, preset.getTitle());
            statement.setString(2, preset.getPurchaseLink());
            statement.setString(3, preset.getImageLink());
            statement.setLong(4, presetId);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException exception) {
            resp.setStatus(Constants.INTERNAL_SERVER_ERROR);
            resp.getWriter().print(Utils.getStackTrace(exception));
        }
    }

    public static Preset getPreset(Connection connection, HttpServletResponse resp, long presetId) throws IOException {
        Preset preset = new Preset();
        try {
            String getUserInfoQuery = "SELECT * FROM Preset WHERE id = ?;";
            PreparedStatement statement = connection.prepareStatement(getUserInfoQuery,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            statement.setLong(1, presetId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.first()) {
                preset.setTitle(resultSet.getString(Constants.TITLE_KEY));
                preset.setPurchaseLink(resultSet.getString(PURCHASE_LINK_KEY));
                preset.setImageLink(resultSet.getString(IMAGE_LINK_KEY));
            } else {
                resp.setStatus(Constants.BAD_REQUEST);
            }
            statement.close();
        } catch (SQLException exception) {
            resp.setStatus(Constants.INTERNAL_SERVER_ERROR);
            resp.getWriter().print(Utils.getStackTrace(exception));
        }
        return preset;
    }
}
