package SuperSecureChat.Contacts;

import javafx.scene.image.Image;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.time.Instant;
import java.util.Base64;

public class Contact {


    private String id;
    private String firstname;
    private String lastname;
    private String url;
    private static final Contact me = new Contact("1234", "Philip", "Schneider", "169.254.162.72", Instant.now().getEpochSecond(), encoder(
            new File(System.getenv("APPDATA") + "\\SuperSecureChat\\profile.png").getAbsolutePath())
            , 0);
    private String image;
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

    public Contact(String id, String firstname, String lastname, String url, long lastOnline, String image, long notifications) {
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

    public Contact(String firstname, String lastname, String url, String image, int notifications) {
        this.id = "test";
        this.firstname = firstname;
        this.lastname = lastname;
        this.url = url;
        this.image = image;
        this.notifications = notifications;
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
                contact.setImage(jsonObject.get("image").toString());
            } else {
                contact.setImage(null);
            }
            contact.setNotifications((long) jsonObject.get("notifications"));
        } catch (ParseException ignored) {
        }
        return contact; //TODO Base64 of Image File
    }

    public static String encoder(String filePath) {
        String base64File = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file = new File(ExportResource("profile.png"));
            }
            FileInputStream imageInFile = new FileInputStream(file);
            // Reading a file from file system
            byte[] fileData = new byte[(int) file.length()];
            imageInFile.read(fileData);
            base64File = Base64.getEncoder().encodeToString(fileData);
        } catch (FileNotFoundException e) {
            System.out.println("File not found" + e);
        } catch (IOException ioe) {
            System.out.println("Exception while reading the file " + ioe);

        }
        return base64File;
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

    public static Image imageDecoder(String base64Image) {
        byte[] imageByteArray = Base64.getDecoder().decode(base64Image);
        return new Image(new ByteArrayInputStream(imageByteArray));
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
            jsonMessage.put("image", image);
        } else {
            jsonMessage.put("image", null);
        }
        jsonMessage.put("notifications", notifications);
        return jsonMessage;
    }

    public String getImage() {
        return image;
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

    public void setImage(String image) {
        this.image = image;
    }

    public Image getJavaFXImage() {
        return imageDecoder(getImage());
    }

    public void setImageFromFilePath(String path) {
        setImage(encoder(path));
    }

    static public String ExportResource(String resourceName) {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String jarFolder = null;
        try {
            stream = Contact.class.getClassLoader().getResourceAsStream(resourceName);//note that each / is a directory down in the "jar tree" been the jar the root of the tree
            if (stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            jarFolder = System.getenv("APPDATA") + "\\SuperSecureChat\\";
            resStreamOut = new FileOutputStream(jarFolder + resourceName);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                stream.close();
                resStreamOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return jarFolder + resourceName;
    }
}
