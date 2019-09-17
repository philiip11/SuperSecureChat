package SuperSecureChat.Controller;

import SuperSecureChat.Chat.ChatListViewCell;
import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Message;
import SuperSecureChat.NotificationHandler;
import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.Comparator;

public class NotificationController {
    @FXML
    ImageView picture;
    @FXML
    JFXListView<Message> messagesListView;
    private Stage stage;
    private Contact contact;
    private ObservableList<Message> messages = FXCollections.observableArrayList();

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
        picture.setImage(contact.getJavaFXImage());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void newMessage(Message message) {
        Platform.runLater(() -> {
            messages.add(message);
            updateListView();
        });
    }


    private void updateListView() {
        messagesListView.setItems(messages);
        messagesListView.setCellFactory(chatListView -> new ChatListViewCell());
        messagesListView.setExpanded(true);
        messagesListView.scrollTo(messages.size() - 1);
        messagesListView.getItems().sort(Comparator.comparingLong(Message::getCreated));
    }

    public void openChat(MouseEvent mouseEvent) {
        ContactsController.getInstance().openChat(contact);
    }

    public void close() {
        stage.close();
        NotificationHandler.getInstance().notificationControllerClosed(this);
    }


}
