package SuperSecureChat.Contacts;

import javafx.scene.image.Image;

public class Contact {

    private String vorname;
    private String nachname;
    private String url;
    private Image image;

    private int notifications;

    public Contact(String vorname, String nachname, String url, Image image, int notifications) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.url = url;
        this.image = image;
        this.notifications = notifications;
    }

    public int getNotifications() {
        return notifications;
    }

    public void setNotifications(int notifications) {
        this.notifications = notifications;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
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
        return vorname + " " + nachname;
    }

    @Override
    public String toString() {
        return getName();
    }

}
