package SuperSecureChat.Controller;

import SuperSecureChat.Chat.ChatListViewCell;
import SuperSecureChat.ClassConnector;
import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Crypto.Crypto;
import SuperSecureChat.Database;
import SuperSecureChat.Message;
import SuperSecureChat.Network.Network;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;

public class ChatController {

    @FXML
    JFXButton sendFile;
    @FXML
    JFXListView<Message> messagesListView;
    @FXML
    JFXButton sendMessage;
    @FXML
    JFXTextField txtMessage;

    private Network network = Network.getInstance();
    private ObservableList<Message> messages = FXCollections.observableArrayList();
    private Stage stage;
    private String data = "";

    private Contact contact;
    private Contact me = Contact.getMyContact();
    private Database database = Database.getInstance();
    private String dataName = null;

    public void setStage(Stage stage) {
        this.stage = stage;
    }


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
        if ((txtMessage.getText() == null || txtMessage.getText().equals("")) && dataName == null) {
            return;
        }
        String text = txtMessage.getText();
        if (dataName != null) {
            text = dataName;
        } else {
            txtMessage.clear();
        }

        Message message = new Message(me.getId() + Instant.now().getEpochSecond(), "", me, contact, text, data, "", Instant.now().getEpochSecond(), 0, 0);
        data = "";
        dataName = null;
        Crypto crypto = new Crypto();
        crypto.setSecretKey(Database.getInstance().getSecretKeyByContact(message.getReceiver()));
        message.setText(crypto.encrypt(message.getText()));
        if (!message.getData().equals("") && message.getData() != null) {
            message.setData(crypto.encrypt(message.getData()));
        }
        network.sendMessage(message);
        messages.add(message);
        updateListView();
        Database.getInstance().newMessage(message);


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


    public void uploadPicture(MouseEvent mouseEvent) {   //TODO einbinden in den Chat
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chatPicture.fxml"));
            Parent root = loader.load();
            ChatPictureController chatPicture = loader.getController();
            Stage stage = new Stage();
            stage.setTitle("Share a picture");
            //
            chatPicture.setStage(stage);
            chatPicture.setChatController(this);
            openNewStage(root, stage, 500, 350);

        } catch (Exception ignored) {

        }

    }

    private void openNewStage(Parent root, Stage stage, int width, int height) {
        JFXDecorator decorator = new JFXDecorator(stage, root);
        decorator.setCustomMaximize(false);
        ImageView imageView = new ImageView();  //TODO richtig so?
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


    public void newMessage(Message message) {
        //if (message.getReceiver() != null && message.getSender() != null) {
        if ((message.getReceiver().getId().equals(me.getId()) &&            // Naichricht von Kontakt an mch
                message.getSender().getId().equals(contact.getId())) ||
                (message.getReceiver().getId().equals(contact.getId()) &&   // Nachricht von mich an Kontakt
                        message.getSender().getId().equals(me.getId()))) {

            ArrayList<Message> remove = new ArrayList<>();
            for (Message m : messages) {
                if (m.getId().equals(message.getId())) {
                    remove.add(m);
                }
            }
            Platform.runLater(() -> {
                for (Message m : remove) {
                    messages.remove(m);
                }
                messages.add(message);
                updateListView();
            });
        }
        //}
    }

    public void sendFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            return;
        }

        dataName = file.getName();
        try {
            data = Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
            buttonClick();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
