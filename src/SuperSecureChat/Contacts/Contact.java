package SuperSecureChat.Contacts;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Base64;

public class Contact {


    private String id;
    private String firstname;
    private String lastname;
    private String url;
    private Image image;
    private static final Contact me = new Contact("1234", "Philip", "Schneider", "169.254.162.72", Instant.now().getEpochSecond(), new Image(Contact.class.getResourceAsStream("/icon.png")), 0);
    private long notifications;
    private long lastOnline;

    public static Contact getMyContact() {
        return me;
    }

    public static void setMyName(String username, String firstname, String lastname) {
        me.setId(username);
        me.setFirstname(firstname);
        me.setLastname(lastname);
    }

    public Contact(String id, String firstname, String lastname, String url, long lastOnline, Image image, long notifications) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.url = url;
        this.lastOnline = lastOnline;
        this.image = image;
        this.notifications = notifications;
    }

    public Contact() {

    }

    public static Contact fromJSON(String json) {
        Contact contact = new Contact();
        try {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(json);
            contact.setId(jsonObject.get("id").toString());
            contact.setFirstname(jsonObject.get("firstname").toString());
            contact.setLastname(jsonObject.get("lastname").toString());
            contact.setUrl((String) jsonObject.get("url"));
            if (jsonObject.get("lastOnline") != null) {
                contact.setLastOnline((long) jsonObject.get("lastOnline"));
            } else {
                contact.setLastOnline(Instant.now().getEpochSecond());
            }
            if (jsonObject.get("image") != null) {
                Image i = new Image(new ByteArrayInputStream(Base64.getDecoder().decode((byte[]) jsonObject.get("image"))));
                contact.setImage(i);
            } else {
                contact.setImage(null);
            }
            contact.setNotifications((long) jsonObject.get("notifications"));
        } catch (ParseException ignored) {
        }
        return contact;
    }

    @SuppressWarnings("unchecked")
    public JSONObject toJSON() {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("id", id);
        jsonMessage.put("firstname", firstname);
        jsonMessage.put("lastname", lastname);
        jsonMessage.put("url", url);
        jsonMessage.put("lastOnline", lastOnline);
        if (image != null) {
            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();

            int w = (int) image.getWidth();
            int h = (int) image.getHeight();

            ByteBuffer buf = ByteBuffer.allocate(w * h * 4);
            //byte[] buf = new byte[w * h * 4];
            image.getPixelReader().getPixels(0, 0, w, h, PixelFormat.getByteBgraInstance(), buf, w * 4);
            byte[] bytes = new byte[buf.remaining()];
            buf.get(bytes);

            jsonMessage.put("image", Base64.getEncoder().encodeToString(bytes));
        } else {
            jsonMessage.put("image", null);
        }
        jsonMessage.put("notifications", notifications);
        return jsonMessage;
    }

    public Contact(String firstname, String lastname, String url, Image image, int notifications) {
        this.id = "test";
        this.firstname = firstname;
        this.lastname = lastname;
        this.url = url;
        this.image = image;
        this.notifications = notifications;
    }

    public String toJSONString() {
        return toJSON().toString();

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

    public long getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(long lastOnline) {
        this.lastOnline = lastOnline;
    }

    public long getNotifications() {
        return notifications;
    }

    public void setNotifications(long notifications) {
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
