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
    private double tx;
    private double ty;
    private double ax;
    private double ay;
    private int CENTER_X;
    private int CENTER_Y;
    private Contact contact;


    public NetworkContact(double x, double y, Contact contact, int CENTER_X, int CENTER_Y) {
        this.x = CENTER_X;
        this.y = CENTER_Y;
        this.CENTER_X = CENTER_X;
        this.CENTER_Y = CENTER_Y;
        tx = x;
        ty = y;
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
        if (contact.getLastOnline() > Instant.now().getEpochSecond() - 15) {
            gc.setFill(Paint.valueOf("#00ff00"));
        } else {
            gc.setFill(Paint.valueOf("#ff0000"));
        }

        calcNewPosition();


        double r = 70 + sin(animator * PI / (NetworkController.ANIMATION_LOOP / 2)) * 6;
        gc.fillOval(x - r / 2, y - r / 2, r, r);
        gc.drawImage(contact.getJavaFXImage(), 0, 0, 256, 256, x - 32, y - 32, 64, 64);


    }

    private void calcNewPosition() {
        if (tx != x) {
            ax += (tx - x) / 100;
            x = x + ax;
            ax = ax * 0.85;
        }
        if (ty != y) {
            ay += (ty - y) / 100;
            y = y + ay;
            ay = ay * 0.85;
        }
    }

    public void moveTo(double x, double y) {
        tx = x;
        ty = y;
    }
}
