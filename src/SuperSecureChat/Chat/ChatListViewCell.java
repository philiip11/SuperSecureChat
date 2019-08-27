package SuperSecureChat.Chat;


import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Crypto.Crypto;
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
            Crypto crypto = new Crypto();
            byte[] secretKey;
            String fxmlResource;
            if (message.getSender().getId().equals(Contact.getMyContact().getId())) {
                fxmlResource = "/fxml/chatCellMe.fxml";
                secretKey = Database.getInstance().getSecretKeyByContact(message.getReceiver());
            } else {
                fxmlResource = "/fxml/chatCell.fxml";
                secretKey = Database.getInstance().getSecretKeyByContact(message.getSender());
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
            //contactImage.setImage(contact.getJavaFXImage());
            //int notifications = db.countUnreadMessagesByContact(contact);
            //badge.setEnabled(notifications > 0);
            //badge.setText(String.valueOf(notifications));


            setText(null);
            setGraphic(anchorPane);
        }

    }

}
