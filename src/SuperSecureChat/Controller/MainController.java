package SuperSecureChat.Controller;

import SuperSecureChat.ClassConnector;
import SuperSecureChat.Contacts.Contact;
import com.sun.jna.platform.win32.Secur32;
import com.sun.jna.ptr.IntByReference;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.Calendar;

public class MainController {

    // TODO Implement Chat

    // TODO Implement GUI via JavaFX
    // TODO Find Users via TCP
    // TODO Encrypt Messages
    // TODO Send Messages via TCP to Every User
    // TODO Filter Messages send to me
    // TODO Save Messages in Database
    // TODO Ping for Online-Status
    // TODO Status und Profilbild
    // TODO Windows-Notifications
    // TODO Add DataMessages

    // TODO Show Network Graphically


    @FXML
    Label usernameLabel;
    @FXML
    Label daytimeLabel;
    //TODO Label einbinden und erstellen.

    private String vorname;
    private String nachname;

    public void initialize() {
        //TODO Set KeyCombos
        daytimeLabel.setText(gettimebycalendar());


        new Thread(this::init).start(); // Daten asynchron laden
    }

    private void init() {
        String fullName = getFullName();
        String[] nameArr = fullName.split(",");
        vorname = nameArr[1].trim();
        nachname = nameArr[0].trim();
        Platform.runLater(() -> usernameLabel.setText(vorname));
        String username = new com.sun.security.auth.module.NTSystem().getName();
        Contact.setMyName(username, vorname, nachname);
        /*try (OutputStream output = new FileOutputStream("config.properties")) {
            Properties prop = new Properties();
            username = new com.sun.security.auth.module.NTSystem().getName(); // Nutzername
            System.out.println(username);
            prop.setProperty("username", username);
            prop.setProperty("vorname", vorname);
            prop.setProperty("nachname", nachname);
            System.out.println(fullName);
            Platform.runLater(() -> {
                usernameLabel.setText(vorname);
            });
            prop.store(output, "");
        } catch (IOException io) {
            io.printStackTrace();
        }*/

        //TODO Datenbank laden
        try {
            Thread.sleep(1500); // Nur zu Testzwecken
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Platform.runLater(this::openContacts);
        Platform.runLater(this::close);
    }

    private void close() {
        Stage stage = (Stage) usernameLabel.getScene().getWindow();
        stage.close();
    }

    private void openContacts() {
        ClassConnector.getInstance().openContacts();
    }

    private String getFullName() {
        char[] name = new char[256];
        Secur32.INSTANCE.GetUserNameEx(
                Secur32.EXTENDED_NAME_FORMAT.NameDisplay,
                name,
                new IntByReference(name.length)
        );
        return new String(name).trim();
    }




    private String gettimebycalendar() {
        Calendar calendar = Calendar.getInstance();
        int timeofday = calendar.get(Calendar.HOUR_OF_DAY);

        if (timeofday < 12) {
            return "Good Morning";
        } else if (timeofday < 16) {
            return "Good Afternoon";
        } else if (timeofday < 21) {
            return "Good Evening";
        } else {
            return "Good Night";
        }

    }


}
