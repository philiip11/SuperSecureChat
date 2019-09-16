package SuperSecureChat.Contacts;

import SuperSecureChat.Controller.ShowPictureController;
import SuperSecureChat.Database;
import com.jfoenix.controls.JFXBadge;
import com.jfoenix.controls.JFXListCell;
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
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.time.Instant;

public class ContactListViewCell extends JFXListCell<Contact> {

    @FXML
    Label label1;
    @FXML
    ImageView contactImage;
    @FXML
    AnchorPane anchorPane;
    @FXML
    JFXBadge badge;

    Contact contacts;
    private FXMLLoader mLLoader;
    Database db = Database.getInstance();

    @Override
    protected void updateItem(Contact contact, boolean empty) {
        super.updateItem(contact, empty);
        if (empty || contact == null) {

            setText(null);
            setGraphic(null);

        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("/fxml/contactCell.fxml"));
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            label1.setText(contact.getFirstname() + " " + contact.getLastname());
            try {
                contactImage.setImage(contact.getJavaFXImage());
            } catch (NullPointerException ignored) {

            }
            contactImage.setOnMouseClicked(this::onPictureClick);
            updateBadge(contact);
            setText(null);
            setGraphic(anchorPane);

        }
        this.contacts = contact;

    }

    private void updateBadge(Contact contact) {
        int notifications = db.countUnreadMessagesByContact(contact);
        badge.setEnabled(true);
        if (notifications > 0) {
            badge.setText(String.valueOf(notifications));
        } else {
            badge.setText("");
        }

        badge.getStyleClass().clear();
        if (contact.getLastOnline() > Instant.now().getEpochSecond() - 15) {
            badge.getStyleClass().add("online");
        } else {
            badge.getStyleClass().add("offline");
        }
    }

    public void onPictureClick(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/showPicture.fxml"));
            Parent root = loader.load();
            ShowPictureController showPictureController = loader.getController();
            Stage stage = new Stage();
            stage.setX(mouseEvent.getScreenX() - 128);
            stage.setY(mouseEvent.getScreenY() - 128);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setTitle(contacts.getFirstname() + " " + contacts.getLastname());
            stage.getIcons().add(getJavaFXImage());
            Scene scene = new Scene(root, 256, 256, true, SceneAntialiasing.BALANCED);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            showPictureController.showPicture(getJavaFXImage());
            showPictureController.setStage(stage);
            stage.show();

            mouseEvent.consume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Image getJavaFXImage() {
        return contacts.getJavaFXImage();
    }
}
