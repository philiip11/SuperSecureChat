package SuperSecureChat.Chat;


import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Controller.ShowPictureController;
import SuperSecureChat.Crypto.Crypto;
import SuperSecureChat.Database;
import SuperSecureChat.Message;
import com.jfoenix.controls.JFXBadge;
import com.jfoenix.controls.JFXListCell;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class ChatListViewCell extends JFXListCell<Message> {

    @FXML
    Text labelMessage;
    @FXML
    Label labelTime;
    @FXML
    Label received;
    @FXML
    ImageView imageView;
    @FXML
    AnchorPane anchorPane;
    @FXML
    JFXBadge badge;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    Database db = Database.getInstance();
    //TODO
    private FXMLLoader mLLoader;

    @Override
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);
        if (empty || message == null) {

            setText(null);
            setGraphic(null);

        } else {
            Crypto crypto = new Crypto();
            byte[] secretKey;
            String fxmlResource;
            boolean messageFromMe;
            boolean dataMessage = false;
            if (message.getSender().getId().equals(Contact.getMyContact().getId())) {
                if (message.getData().equals("") || message.getData() == null) {
                    fxmlResource = "/fxml/chatCellMe.fxml";
                } else {
                    fxmlResource = "/fxml/chatCellPictureMe.fxml";
                    dataMessage = true;
                }
                secretKey = Database.getInstance().getSecretKeyByContact(message.getReceiver());
                messageFromMe = true;
            } else {
                if (message.getData().equals("") || message.getData() == null) {
                    fxmlResource = "/fxml/chatCell.fxml";
                } else {
                    fxmlResource = "/fxml/chatCellPicture.fxml";
                    dataMessage = true;
                }
                secretKey = Database.getInstance().getSecretKeyByContact(message.getSender());
                messageFromMe = false;
            }
            crypto.setSecretKey(secretKey);
            mLLoader = new FXMLLoader(getClass().getResource(fxmlResource));
            mLLoader.setController(this);

            try {
                mLLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            labelMessage.setText(crypto.decrypt(message.getText()));
            labelTime.setText(simpleDateFormat.format(new Date(message.getCreated() * 1000L)));
            if (messageFromMe) {
                received.setVisible(message.getReceived() > 0);
            }
            if (dataMessage) {
                String filename = crypto.decrypt(message.getText());
                //TODO Dateien verschlüsseln
                if (filename.contains(".png") ||
                        filename.contains(".jpg") ||
                        filename.contains(".jfif")) {
                    Image image = Contact.imageDecoder(message.getData());
                    imageView.setImage(image);
                    imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            try {
                                double width = image.getWidth();
                                double height = image.getHeight();
                                double screenHeight = Screen.getPrimary().getBounds().getHeight();
                                double screenWidth = Screen.getPrimary().getBounds().getWidth();
                                width = Math.min(width, screenWidth);
                                height = Math.min(height, screenHeight);
                                double x = (screenWidth - width) / 2;
                                double y = (screenHeight - height) / 2;

                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/showPicture.fxml"));
                                Parent root = loader.load();
                                ShowPictureController showPictureController = loader.getController();
                                Stage stage = new Stage();
                                stage.setX(x);
                                stage.setY(y);
                                stage.initStyle(StageStyle.TRANSPARENT);
                                stage.setTitle(message.getText());
                                stage.getIcons().add(image);
                                Scene scene = new Scene(root, width, height, true, SceneAntialiasing.BALANCED);
                                scene.setFill(Color.TRANSPARENT);
                                stage.setScene(scene);
                                showPictureController.showPicture(image, width, height);
                                showPictureController.setStage(stage);
                                stage.show();
                                event.consume();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    imageView.setImage(new Image(getClass().getResourceAsStream("/icons/round_attach_file_white_48dp.png")));
                    imageView.setOnMouseClicked(event -> {
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.setInitialFileName(filename);
                        File file = fileChooser.showSaveDialog(null);
                        if (file != null) {
                            try {
                                Files.write(file.toPath(), Base64.getDecoder().decode(message.getData()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                    });
                }

            }

            //contactImage.setImage(contact.getJavaFXImage());
            //int notifications = db.countUnreadMessagesByContact(contact);
            //badge.setEnabled(notifications > 0);
            //badge.setText(String.valueOf(notifications));


            setText(null);
            setGraphic(anchorPane);
        }

    }

}
