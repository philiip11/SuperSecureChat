package SuperSecureChat.Controller;

import SuperSecureChat.Chat.ChatListViewCell;
import SuperSecureChat.ClassConnector;
import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Crypto.Crypto;
import SuperSecureChat.Database;
import SuperSecureChat.Message;
import SuperSecureChat.Network.Network;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import emoji4j.EmojiUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.input.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;

public class ChatController {

    @FXML
    JFXButton sendFile;
    @FXML
    JFXListView<Message> messagesListView;
    @FXML
    JFXButton sendMessage;
    @FXML
    JFXButton openEmojiPicker;
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

    private int postId = 0;

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    public void setContact(Contact contact) {
        this.contact = contact;
        messages.addAll(database.getMessagesByContacts(contact, me));
        Platform.runLater(this::updateListView);
    }

    @FXML
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
            text = EmojiUtils.emojify(text);
            txtMessage.clear();
        }

        Message message = new Message(me.getId() + Instant.now().getEpochSecond() + postId++, "", me, contact, text, data, "", Instant.now().getEpochSecond(), 0, 0);
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
        Database.getInstance().updateMessage(message);


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
        if (message.getReceiver() != null && message.getSender() != null && contact != null && contact.getId() != null) {
            if ((message.getReceiver().getId().equals(me.getId()) &&            // Nachricht von Kontakt an mch
                    message.getSender().getId().equals(contact.getId())) ||
                    (message.getReceiver().getId().equals(contact.getId()) &&   // Nachricht von mich an Kontakt
                            message.getSender().getId().equals(me.getId()))) {

                if (!message.getData().equals("DELDATA:THIS")) {
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
                            messagesListView.getItems().sort(Comparator.comparingLong(Message::getCreated));

                        }
                    });
                }
            }
        } else {
            System.out.println("ChatControllerNullPointerException");
            System.out.println("me.getId(): " + me.getId());
            System.out.println("contact.getId(): " + contact.getId());
            System.out.println("message.getText(): " + message.getText());
            System.out.println("message.getSender().getId(): " + message.getSender().getId());
            System.out.println("message.getReceiver().getId(): " + message.getReceiver().getId());
        }
    }

    public void showFileChooser(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        List<File> files = fileChooser.showOpenMultipleDialog(stage);
        sendFiles(files);
    }

    private void sendFiles(List<File> files) {
        if (files.size() == 0) {
            return;
        }
        for (File file : files) {
            dataName = file.getName();
            try {
                data = Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
                buttonClick();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void openEmojiPicker(ActionEvent actionEvent) {
        try {
            int height = 400;
            int width = 350;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/emojiPicker.fxml"));
            Parent root = loader.load();
            EmojiPickerController emojiPickerController = loader.getController();
            Stage stage = new Stage();
            Bounds boundsInScreen = openEmojiPicker.localToScreen(openEmojiPicker.getBoundsInLocal());

            stage.setY(boundsInScreen.getMaxY() - 64 - height);
            stage.setX(boundsInScreen.getMaxX() - width);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setTitle("Emojipicker");
            stage.getIcons().add(contact.getJavaFXImage());
            Scene scene = new Scene(root, width, height, true, SceneAntialiasing.BALANCED);
            final ObservableList<String> stylesheets = scene.getStylesheets();
            stylesheets.addAll(getClass().getResource("/css/jfoenix-fonts.css").toExternalForm(),
                    getClass().getResource("/css/jfoenix-design.css").toExternalForm(),
                    getClass().getResource("/css/custom.css").toExternalForm(),
                    getClass().getResource("/css/jfoenix-main-demo.css").toExternalForm(),
                    getClass().getResource("/css/super-secure-chat.css").toExternalForm());
            stage.setScene(scene);
            emojiPickerController.setStage(stage);
            emojiPickerController.setChatController(this);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void add(String text) {
        txtMessage.setText(txtMessage.getText() + text);
    }

    public Contact getContact() {
        return contact;
    }

    void show() {
        stage.show();
        stage.toFront();
    }

    public boolean isShowing() {
        return stage.isShowing();
    }

    public void onDragDropped(DragEvent dragEvent) {
        Dragboard dragboard = dragEvent.getDragboard();
        boolean success = false;
        if (dragboard.hasFiles()) {
            sendFiles(dragboard.getFiles());
            success = true;
        }
        dragEvent.setDropCompleted(success);
        dragEvent.consume();

    }

    public void onDragOver(DragEvent dragEvent) {
        if (dragEvent.getDragboard().hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.COPY);
        }
        dragEvent.consume();
    }
}
