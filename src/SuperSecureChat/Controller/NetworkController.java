package SuperSecureChat.Controller;

import SuperSecureChat.ClassConnector;
import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Contacts.ContactList;
import SuperSecureChat.Message;
import SuperSecureChat.NetworkMap.NetworkContact;
import SuperSecureChat.NetworkMap.NetworkContactMessage;
import SuperSecureChat.NetworkMap.NetworkIconMessage;
import SuperSecureChat.NetworkMap.NetworkMessage;
import javafx.animation.AnimationTimer;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;

import static java.lang.Math.*;

@SuppressWarnings({"FieldCanBeLocal"})
public class NetworkController {
    public final static int ANIMATION_LOOP = 60;
    private final int RADIUS = 360;
    private final int WIDTH = 1280;
    private final int HEIGHT = 1024;
    private final int CENTER_X = WIDTH / 2;
    private final int CENTER_Y = HEIGHT / 2;
    @FXML
    Canvas canvas;
    private int animator = 0;
    private ContactList contactList = ContactList.getInstance();
    private ArrayList<NetworkContact> networkContactList = new ArrayList<>();
    private final ArrayList<NetworkMessage> networkMessages = new ArrayList<>();

    @FXML
    public void initialize() {
        ObservableList<Contact> allContacts = contactList.getAllContacts();
        int size = allContacts.size();
        for (int i = 0; i < size; i++) {
            addContact(size + 1, i + 1, allContacts.get(i));
        }
        addContact(size + 1, 0, Contact.getMyContact());

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                draw();
            }
        };
        timer.start();
        ClassConnector.getInstance().addNetworkController(this);
    }

    private void addContact(int size, double i, Contact contact) {
        double x = CENTER_X + sin((i / (double) size) * PI * 2) * RADIUS;
        double y = CENTER_Y + cos((i / (double) size) * PI * 2) * RADIUS;
        networkContactList.add(new NetworkContact(x, y, contact));
    }

    private void draw() {
        animator++;
        animator = animator % ANIMATION_LOOP;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, WIDTH, HEIGHT);
        synchronized (networkMessages) {
            for (NetworkContact contact : networkContactList) {
                contact.draw(gc, animator);
            }
            for (NetworkMessage message : networkMessages) {
                message.draw(gc);
            }
            networkMessages.removeIf(NetworkMessage::getDelete);
        }
    }


    public void newMessage(Message message, Message m) {
        networkMessages.add(new NetworkMessage(getNetworkContactByContact(m.getSender()), getNetworkContactByContact(m.getReceiver())));
    }

    private NetworkContact getNetworkContactByContact(Contact contact) {
        for (NetworkContact c : networkContactList) {
            System.out.println(c.getContact().getId());
            if (c.getContact().getId().equals(contact.getId())) {
                return c;
            }
        }
        return networkContactList.get(0);
    }

    public void newContact(Contact contact, Message message) {
        networkMessages.add(new NetworkContactMessage(contact, getNetworkContactByContact(message.getSender()), getNetworkContactByContact(message.getReceiver())));

    }

    public void newIconMessage(Image image, Message message) {
        networkMessages.add(new NetworkIconMessage(image, getNetworkContactByContact(message.getSender()), getNetworkContactByContact(message.getReceiver())));
    }
}
