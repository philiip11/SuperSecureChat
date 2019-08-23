package SuperSecureChat.Chat;


import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Database;
import SuperSecureChat.Message;
import com.jfoenix.controls.JFXBadge;
import com.jfoenix.controls.JFXListCell;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatListViewCell extends JFXListCell<Message> {

    @FXML
    Text labelMessage;
    @FXML
    Label labelTime;
    @FXML
    ImageView contactImage;
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
            String fxmlResource = "/fxml/chatCell.fxml";
            if (message.getSender().getId().equals(Contact.getMyContact().getId())) {
                fxmlResource = "/fxml/chatCellMe.fxml";
                System.out.println(message.getSender().getId() + " == " + Contact.getMyContact().getId());

            } else {
                System.out.println(message.getSender().getId() + " != " + Contact.getMyContact().getId());
            }
            mLLoader = new FXMLLoader(getClass().getResource(fxmlResource));
            mLLoader.setController(this);

            try {
                mLLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            labelMessage.setText(message.getText());
            labelTime.setText(simpleDateFormat.format(new Date(message.getCreated() * 1000L)));
            //contactImage.setImage(contact.getJavaFXImage());
            //int notifications = db.countUnreadMessagesByContact(contact);
            //badge.setEnabled(notifications > 0);
            //badge.setText(String.valueOf(notifications));


            setText(null);
            setGraphic(anchorPane);
        }

    }

}
