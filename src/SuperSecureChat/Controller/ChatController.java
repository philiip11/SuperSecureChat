package SuperSecureChat.Controller;

import SuperSecureChat.Chat.ChatListViewCell;
import SuperSecureChat.ClassConnector;
import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Database;
import SuperSecureChat.Message;
import SuperSecureChat.Network.Network;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.time.Instant;
import java.util.Comparator;

public class ChatController {


    @FXML
    JFXListView<Message> messagesListView;
    @FXML
    JFXButton sendMessage;
    @FXML
    JFXTextField txtMessage;

    private Network network = Network.getInstance();
    private ObservableList<Message> messages = FXCollections.observableArrayList();


    private Contact contact;
    private Contact me = Contact.getMyContact();
    private Database database = Database.getInstance();

    public void setContact(Contact contact) {
        this.contact = contact;
        messages.addAll(database.getMessagesByContacts(contact, me));
        Platform.runLater(this::updateListView);
    }

    public void initialize() {
        ClassConnector.getInstance().addChatController(this);
        //Code
    }

    public void buttonClick() {
        if (txtMessage.getText() == null || txtMessage.getText().equals("")) {
            return;
        }

        Message message = new Message(me.getId() + Instant.now().getEpochSecond(), "", me, contact, txtMessage.getText(), "", "", Instant.now().getEpochSecond(), 0, 0);

        messages.add(message);
        updateListView();
        network.sendMessage(message);
        Database.getInstance().newMessage(message);

        txtMessage.clear();

    }

    private void updateListView() {
        messagesListView.setItems(messages);
        messagesListView.setCellFactory(chatListView -> new ChatListViewCell());
        messagesListView.setOnMouseClicked(this::onMessageClicked);
        messagesListView.setExpanded(true);
        messagesListView.scrollTo(messages.size() - 1);
        messagesListView.getItems().sort(Comparator.comparingLong(Message::getCreated));
    }

    private void onMessageClicked(MouseEvent mouseEvent) {
    }


    public void txtOnKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            buttonClick();
            txtMessage.clear();
        }
    }


    public void newMessage(Message message) {
        if ((message.getReceiver().getId().equals(me.getId()) &&            // Nachricht von Kontakt an mich
                message.getSender().getId().equals(contact.getId())) ||
                (message.getReceiver().getId().equals(contact.getId()) &&   // Nachricht von mich an Kontakt
                        message.getSender().getId().equals(me.getId()))) {

            for (Message m : messages) {
                if (m.getId().equals(message.getId())) {
                    return;
                }
            }
            Platform.runLater(() -> messages.add(message));
            Platform.runLater(this::updateListView);
        }
    }
}
