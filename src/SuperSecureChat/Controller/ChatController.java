package SuperSecureChat.Controller;

import SuperSecureChat.Contacts.Contact;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class ChatController {


    @FXML
    JFXButton sendMessage;
    @FXML
    JFXTextField txtMessage;
    @FXML
    JFXListView showMessage;


    private Contact contact;

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public void initialize() {
        //Code
    }

    public void buttonClick() {
        if (txtMessage.getText() == null || txtMessage.getText().equals("")) {
            txtMessage.setText("Keine leeren Eingaben");
        }
        showMessage.getItems().add(txtMessage.getText());                                      //set Text from the Textfield to the Label
        txtMessage.clear();
        //addToHistory();


    }

//    private void addToHistory(){
//
//        history.getItems().add(showMessage);
//
//        //history.getItems().add(0,new Label(text));  //showMessage Label einbinden?
//        //history.setExpanded(true);
//    }


    public void txtOnKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            buttonClick();
        }
    }


}
