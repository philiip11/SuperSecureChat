package SuperSecureChat.Contacts;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class ContactListViewCell extends ListCell<Contact> {

    @FXML
    Label label1;
    @FXML
    ImageView contactImage;
    @FXML
    AnchorPane anchorPane;


    //TODO
    private FXMLLoader mLLoader;

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
            label1.setText(contact.getVorname() + " " + contact.getNachname());
            contactImage.setImage(contact.getImage());


            setText(null);
            setGraphic(anchorPane);
        }

    }

}
