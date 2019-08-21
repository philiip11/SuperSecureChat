package SuperSecureChat.Controller;

import SuperSecureChat.Contacts.Contact;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;

public class ChatController {

    @FXML
    JFXButton sendMessage;

    private Contact contact;

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public void initialize() {

    }

    public void buttonClick() {

    }
}
