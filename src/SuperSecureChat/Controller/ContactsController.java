package SuperSecureChat.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class ContactsController {

    String vorname;
    String nachname;
    String username;

    @FXML
    Label nameLabel;

    public void initialize() {
        //TODO Set KeyCombos

        new Thread(this::init).start();
    }

    private void init() {

        try (InputStream input = new FileInputStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            vorname = prop.getProperty("vorname");
            nachname = prop.getProperty("nachname");
            username = prop.getProperty("username");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        nameLabel.setText(vorname + " " + nachname);

        //TODO Load All Contacts

    }
}
