package SuperSecureChat.NetworkMap;

import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Controller.NetworkController;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

import java.time.Instant;

import static java.lang.Math.PI;
import static java.lang.Math.sin;

@SuppressWarnings({"IntegerDivisionInFloatingPointContext", "FieldCanBeLocal"})
public class NetworkContact {
    private double x;
    private double y;
    private Contact contact;


    public NetworkContact(double x, double y, Contact contact) {
        this.x = x;
        this.y = y;
        this.contact = contact;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public void draw(GraphicsContext gc, int animator) {
        if (contact.getLastOnline() > Instant.now().getEpochSecond() - 11) {
            gc.setFill(Paint.valueOf("#00ff00"));
        } else {
            gc.setFill(Paint.valueOf("#ff0000"));
        }

        double r = 76 + sin(animator * PI / (NetworkController.ANIMATION_LOOP / 2)) * 12;

        gc.fillOval(x - r / 2, y - r / 2, r, r);
        gc.drawImage(contact.getJavaFXImage(), 0, 0, 256, 256, x - 32, y - 32, 64, 64);

    }
}
