package SuperSecureChat.Contacts;

import SuperSecureChat.Database;
import com.jfoenix.controls.JFXBadge;
import com.jfoenix.controls.JFXListCell;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

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


    //TODO
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
            contactImage.setImage(contact.getJavaFXImage());
            int notifications = db.countUnreadMessagesByContact(contact);
            badge.setEnabled(true);
            if (notifications > 0) {
                badge.setText(String.valueOf(notifications));
            } else {
                badge.setText("");
            }

            if (contact.getLastOnline() > Instant.now().getEpochSecond() - 15) {
                int i = badge.getStyleClass().indexOf("offline");
                if (i != -1) {
                    badge.getStyleClass().remove(i);
                }
                badge.getStyleClass().add("online");
            } else {
                int i = badge.getStyleClass().indexOf("online");
                if (i != -1) {
                    badge.getStyleClass().remove(i);
                }
                badge.getStyleClass().add("offline");
            }

            setText(null);
            setGraphic(anchorPane);
        }

    }

}
