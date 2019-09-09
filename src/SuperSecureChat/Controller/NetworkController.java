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
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.Math.*;

@SuppressWarnings({"FieldCanBeLocal"})
public class NetworkController {

    //TODO Respone Message after 1st Message

    public final static int ANIMATION_LOOP = 120;
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
    private final CopyOnWriteArrayList<NetworkMessage> networkMessages = new CopyOnWriteArrayList<>();

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
        for (NetworkContact contact : networkContactList) {
            contact.draw(gc, animator);
        }
        int i = 0;
        for (NetworkMessage message : networkMessages) {
            i++;
            if (i < 25) {
                message.draw(gc);
            }

        }
        networkMessages.removeIf(NetworkMessage::getDelete);
    }


    public NetworkMessage newMessage(Message message, Message m) {
        NetworkMessage networkMessage = new NetworkMessage(message.getText(), getNetworkContactByContact(m.getSender()), getNetworkContactByContact(m.getReceiver()));
        networkMessages.add(networkMessage);
        return networkMessage;
    }

    public NetworkContact getNetworkContactByContact(Contact contact) {
        for (NetworkContact c : networkContactList) {
            if (c.getContact().getId().equals(contact.getId())) {
                return c;
            }
        }
        return networkContactList.get(0);
    }

    public NetworkContactMessage newContact(Contact contact, Message message) {
        NetworkContactMessage networkContactMessage = new NetworkContactMessage(contact, getNetworkContactByContact(message.getSender()), getNetworkContactByContact(message.getReceiver()));
        networkMessages.add(networkContactMessage);
        return networkContactMessage;

    }

    public NetworkIconMessage newIconMessage(Image image, Message message) {
        NetworkIconMessage networkIconMessage = new NetworkIconMessage(image, getNetworkContactByContact(message.getSender()), getNetworkContactByContact(message.getReceiver()));
        networkMessages.add(networkIconMessage);
        return networkIconMessage;
    }

    public NetworkMessage newNetworkMessage(NetworkMessage message) {
        networkMessages.add(message);
        return message;
    }
}
