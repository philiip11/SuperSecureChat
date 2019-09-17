package SuperSecureChat.Controller;

import SuperSecureChat.Chat.ChatListViewCell;
import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Message;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
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
        messages.add(message);
        Platform.runLater(this::updateListView);
    }


    private void updateListView() {
        messagesListView.setItems(messages);
        messagesListView.setCellFactory(chatListView -> new ChatListViewCell());
        messagesListView.setExpanded(true);
        messagesListView.scrollTo(messages.size() - 1);
        messagesListView.getItems().sort(Comparator.comparingLong(Message::getCreated));
    }

    public void openChat(MouseEvent mouseEvent) {
        int width = 500;
        int height = 800;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chat.fxml"));
            Stage stage = new Stage();
            Parent root = loader.load();
            ChatController chatController = loader.getController();
            chatController.setContact(contact);
            chatController.setStage(stage);
            stage.setTitle(contact.getName());
            stage.getIcons().add(contact.getJavaFXImage());
            openNewStage(root, stage, width, height, contact.getJavaFXImage());
        } catch (Exception ignored) {

        }
    }

    public void close(MouseEvent mouseEvent) {
        stage.close();
    }

    private void openNewStage(Parent root, Stage stage, int width, int height, Image image) {
        JFXDecorator decorator = new JFXDecorator(stage, root);
        decorator.setCustomMaximize(false);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(32);
        imageView.setFitWidth(32);
        decorator.setGraphic(imageView);
        Scene scene = new Scene(decorator, width, height, true, SceneAntialiasing.BALANCED);
        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(getClass().getResource("/css/jfoenix-fonts.css").toExternalForm(),
                getClass().getResource("/css/jfoenix-design.css").toExternalForm(),
                getClass().getResource("/css/custom.css").toExternalForm(),
                getClass().getResource("/css/jfoenix-main-demo.css").toExternalForm(),
                getClass().getResource("/css/super-secure-chat.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }
}
