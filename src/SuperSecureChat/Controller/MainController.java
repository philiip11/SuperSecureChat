package SuperSecureChat.Controller;

import SuperSecureChat.Contacts.Contact;
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
        int width = 400;
        int height = 800;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/contacts.fxml"));

            Parent root = loader.load();
            ContactsController contactsController = loader.getController();
            contactsController.setData(vorname, nachname);
            contactsController.setInstance(contactsController);
            Stage stage = new Stage();
            contactsController.setStage(stage);
            stage.setTitle("SSC - Kontakte");
            stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/icon2048.png")));
            stage.setX(Screen.getPrimary().getVisualBounds().getMaxX() - width);
            JFXDecorator decorator = new JFXDecorator(stage, root);
            decorator.setCustomMaximize(false);
            decorator.setGraphic(new ImageView(this.getClass().getResource("/icon16.png").toExternalForm()));
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
