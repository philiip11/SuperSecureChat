package SuperSecureChat.Controller;

import com.jfoenix.controls.JFXDecorator;
import com.sun.jna.platform.win32.Secur32;
import com.sun.jna.ptr.IntByReference;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

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

    private String vorname;
    private String nachname;

    public void initialize() {
        //TODO Set KeyCombos

        new Thread(this::init).start(); // Daten asynchron laden
    }

    private void init() {
        try (OutputStream output = new FileOutputStream("config.properties")) {
            Properties prop = new Properties();

            String username = new com.sun.security.auth.module.NTSystem().getName(); // Nutzername
            System.out.println(username);
            prop.setProperty("username", username);
            String fullName = getFullName();
            String[] nameArr = fullName.split(",");
            vorname = nameArr[1].trim();
            nachname = nameArr[0].trim();
            prop.setProperty("vorname", vorname);
            prop.setProperty("nachname", nachname);
            System.out.println(fullName);
            Platform.runLater(() -> {
                usernameLabel.setText(vorname);
            });
            prop.store(output, "");

            //TODO Datenbank laden
            try {
                Thread.sleep(2000); // Nur zu Testzwecken
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(this::openContacts);
            Platform.runLater(this::close);

        } catch (IOException io) {
            io.printStackTrace();
        }

    }

    private void close() {
        Stage stage = (Stage) usernameLabel.getScene().getWindow();
        stage.close();
    }

    private void openContacts() {
        int width = 400;
        int height = 800;
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/contacts.fxml"));
            Stage stage = new Stage();
            stage.setTitle("SSC - Kontakte");
            stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/icon256.png")));
            stage.setX(Screen.getPrimary().getVisualBounds().getMaxX() - width);
            JFXDecorator decorator = new JFXDecorator(stage, root);
            decorator.setCustomMaximize(false);
            decorator.setGraphic(new ImageView(this.getClass().getResource("/icon32.png").toExternalForm()));
            Scene scene = new Scene(decorator, width, height, true, SceneAntialiasing.BALANCED);

            final ObservableList<String> stylesheets = scene.getStylesheets();
            stylesheets.addAll(getClass().getResource("/css/jfoenix-fonts.css").toExternalForm(),
                    getClass().getResource("/css/jfoenix-design.css").toExternalForm(),
                    getClass().getResource("/css/custom.css").toExternalForm(),
                    getClass().getResource("/css/jfoenix-main-demo.css").toExternalForm(),
                    getClass().getResource("/css/super-secure-chat.css").toExternalForm());

            stage.setScene(scene);
            stage.show();
        } catch (Exception ignored) {

        }
    }

    private void openNewStage(String fxml) {
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

}
