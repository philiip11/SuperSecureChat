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

import java.util.ArrayList;
import java.util.Comparator;

public class NotificationController {
    @FXML
    ImageView picture;
    @FXML
    JFXListView<Message> messagesListViewNotification;
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

    @SuppressWarnings("DuplicatedCode")
    public void newMessage(Message message) {
        Platform.runLater(() -> {
            ArrayList<Message> remove = new ArrayList<>();
            boolean update = true;
            for (Message m : messages) {
                if (m.getId().equals(message.getId())) {
                    remove.add(m);
                    update = false;
                }
            }
            boolean finalUpdate = update;
            for (Message m : remove) {
                messages.remove(m);
            }
            messages.add(message);
            updateListView();
            if (finalUpdate) {
                updateListView();
            } else {
                messagesListViewNotification.getItems().sort(Comparator.comparingLong(Message::getCreated));

            }
        });
    }


    private void updateListView() {
        messagesListViewNotification.setItems(messages);
        messagesListViewNotification.setCellFactory(chatListView -> new ChatListViewCell());
        messagesListViewNotification.setExpanded(true);
        messagesListViewNotification.scrollTo(messages.size() - 1);
        messagesListViewNotification.getItems().sort(Comparator.comparingLong(Message::getCreated));
    }

    public void openChat(MouseEvent mouseEvent) {
        ContactsController.getInstance().openChat(contact);
    }

    public void close() {
        stage.close();
        NotificationHandler.getInstance().notificationControllerClosed(this);
    }


}
