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
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.Math.*;

@SuppressWarnings({"FieldCanBeLocal"})
public class NetworkController {


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
    private CopyOnWriteArrayList<NetworkContact> networkContactList = new CopyOnWriteArrayList<>();
    private final Stack<NetworkMessage> networkMessages = new Stack<>();
    private final CopyOnWriteArrayList<NetworkMessage> drawingMessages = new CopyOnWriteArrayList<>();

    @FXML
    public void initialize() {
        updateContactList();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                draw();
            }
        };
        timer.start();
        ClassConnector.getInstance().addNetworkController(this);
        ObservableList<Contact> allContacts = contactList.getAllContacts();
        networkContactList.clear();
        int size = allContacts.size();
        for (int i = 0; i < size; i++) {
            addContact(size + 1, i + 1, allContacts.get(i));
        }
        addContact(size + 1, 0, Contact.getMyContact());
        /*Message m = new Message();        // Test message for notification testing
        m.setSender(Contact.getMyContact());
        m.setReceiver(Contact.getMyContact());
        m.setId("0");
        m.setText("Lorem ipsum dolor sit amet");
        ClassConnector.getInstance().sendMessageToAllChatControllers(m, true);*/
    }

    public void updateContactList() {
        ArrayList<Contact> allContacts = new ArrayList<>(contactList.getAllContacts());
        ArrayList<Contact> newContacts = new ArrayList<>(allContacts);
        for (NetworkContact nc : networkContactList) {
            newContacts.removeIf(contact -> contact.getId().equals(nc.getContact().getId()));
        }

        int size = allContacts.size();
        int sizenew = newContacts.size();
        int j = 0;
        for (NetworkContact nc : networkContactList) {
            moveContact(size + 1, j + 1, nc);
            j++;
        }
        for (int i = size - sizenew; i < sizenew; i++) {
            addContact(size + 1, i + 1, newContacts.get(i - size + sizenew));
        }


    }

    private void moveContact(int size, int i, NetworkContact nc) {
        double x = CENTER_X + sin((i / (double) size) * PI * 2) * RADIUS;
        double y = CENTER_Y + cos((i / (double) size) * PI * 2) * RADIUS;
        nc.moveTo(x, y);
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
        for (NetworkMessage message : drawingMessages) {
            i++;
            if (i < 100) {
                message.draw(gc);
            }

        }
        drawingMessages.removeIf(NetworkMessage::getDelete);
        if (animator % 5 == 0) {
            if (!networkMessages.empty()) {
                drawingMessages.add(networkMessages.pop());
            }
        }
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

    public NetworkMessage newNetworkMessage(NetworkMessage message, boolean now) {
        if (now) {
            drawingMessages.add(message);
        } else {
            networkMessages.add(message);
        }
        return message;
    }
}
