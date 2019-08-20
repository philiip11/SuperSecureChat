package SuperSecureChat.Controller;

import com.sun.jna.platform.win32.Secur32;
import com.sun.jna.ptr.IntByReference;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

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

    String vorname;
    String nachname;

    public void initialize(){
        //TODO Set KeyCombos

        new Thread(this::init).start(); // Daten asynchron laden
    }

    private void init(){

        String username = new com.sun.security.auth.module.NTSystem().getName(); // Nutzername
        System.out.println(username);
        String fullName = getFullName();
        String[] nameArr = fullName.split(",");
        vorname = nameArr[1].trim();
        nachname = nameArr[0].trim();
        System.out.println(fullName);
        Platform.runLater(()->{usernameLabel.setText(vorname);});

        //TODO Datenbank laden


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

    //TODO Begrüßung nach Tageszeit


    //
    //
    // TCP
    // Nutzer finden über Netzwerknachrichten
    // Ping -- Online?
    //
}
