package SuperSecureChat.Controller;

import SuperSecureChat.Chat.ChatListViewCell;
import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Message;
import SuperSecureChat.Network.Network;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.time.Instant;

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

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public void initialize() {
        //Code
    }

    public void buttonClick() {
        if (txtMessage.getText() == null || txtMessage.getText().equals("")) {
            return;
        }

        Message m = new Message(me.getId() + Instant.now().getEpochSecond(), "", me, contact, txtMessage.getText(), "", "", Instant.now().getEpochSecond(), 0, 0);

        messages.add(m);
        messagesListView.getItems().add(m);
        messagesListView.setCellFactory(chatListView -> new ChatListViewCell());
        messagesListView.setOnMouseClicked(this::onMessageClicked);
        messagesListView.setExpanded(true);
        network.sendMessage(m);

        txtMessage.clear();

    }

    private void onMessageClicked(MouseEvent mouseEvent) {
    }


    public void txtOnKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            buttonClick();
            txtMessage.clear();
        }
    }


}
