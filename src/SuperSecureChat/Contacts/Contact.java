package SuperSecureChat.Contacts;

import javafx.scene.image.Image;
import org.json.simple.JSONObject;

import java.util.Date;

public class Contact {


    private String id;
    private String firstname;
    private String lastname;
    private String url;
    private Date lastOnline;
    private Image image;
    private int notifications;
    private static final Contact me = new Contact();


    public Contact() {

    }

    public static Contact getMyContact() {
        return me;
    }

    public static void setMyName(String username, String firstname, String lastname) {
        me.setId(username);
        me.setFirstname(firstname);
        me.setLastname(lastname);
    }

    public String toJSONString() {
        return toJSON().toJSONString();

    }

    @SuppressWarnings("unchecked")
    public JSONObject toJSON() {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("id", id);
        jsonMessage.put("firstname", firstname);
        jsonMessage.put("lastname", lastname);
        jsonMessage.put("url", url);
        jsonMessage.put("lastOnline", lastOnline);
        jsonMessage.put("image", image);
        jsonMessage.put("notifications", notifications);
        return jsonMessage;
    }

    public Contact(String id, String firstname, String lastname, String url, Date lastOnline, Image image, int notifications) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.url = url;
        this.lastOnline = lastOnline;
        this.image = image;
        this.notifications = notifications;
    }

    public Contact(String firstname, String lastname, String url, Image image, int notifications) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.url = url;
        this.image = image;
        this.notifications = notifications;
    }

    public static void setMyIP(String ip) {
        me.setUrl(ip);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(Date lastOnline) {
        this.lastOnline = lastOnline;
    }

    public int getNotifications() {
        return notifications;
    }

    public void setNotifications(int notifications) {
        this.notifications = notifications;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getName() {
        return firstname + " " + lastname;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "vorname='" + firstname + '\'' +
                ", nachname='" + lastname + '\'' +
                ", url='" + url + '\'' +
                ", image=" + image +
                ", notifications=" + notifications +
                '}';
    }

}
