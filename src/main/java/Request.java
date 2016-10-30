/**
 * Created by alexanderchiou on 7/27/16.
 */
public class Request {
    public static final String USER_ID_KEY = "user_id";
    public static final String LINK_KEY = "link";
    public static final String BODY_KEY = "body";
    public static final String STATE_KEY = "state";
    public static final String TIME_UPDATED_KEY = "time_updated";

    private String title;
    private String link;
    private String body;
    private String state;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
