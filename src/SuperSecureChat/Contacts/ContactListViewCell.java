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
            badge.setEnabled(notifications > 0);
            badge.setText(String.valueOf(notifications));


            setText(null);
            setGraphic(anchorPane);
        }

    }

}
