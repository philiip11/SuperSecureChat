package SuperSecureChat.Controller;

import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Contacts.ContactList;
import SuperSecureChat.Contacts.ContactListViewCell;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXListView;
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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;


public class ContactsController {

    @FXML
    JFXListView<Contact> contactJFXListView;
    private String vorname;
    private String nachname;
    private String username;
    @FXML
    Label nameLabel;
    private ObservableList<Contact> contacts;
    private ContactList contactList = ContactList.getInstance();

    public void initialize() {
        //TODO Set KeyCombos

        new Thread(this::init).start();
    }

    private void init() {

        /*try (InputStream input = new FileInputStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            vorname = prop.getProperty("vorname");
            nachname = prop.getProperty("nachname");
            username = prop.getProperty("username");
        } catch (IOException ex) {
            ex.printStackTrace();
        }*/


        loadContacts();
        Platform.runLater(this::showContacts);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> showContacts());
            }
        }, 5000);


    }

    private void loadContacts() {
        //TODO Load Real Contacts from Database
        contacts = contactList.getAllContacts();
        contacts.addAll(
                new Contact("Max", "Mustermann", null, new Image(getClass().getResourceAsStream("/icon.png")), 4),
                new Contact("John", "Smith", null, null, 0),
                new Contact("John", "Doe", null, null, 0),
                new Contact("Jane", "Doe", null, null, 1),
                new Contact("Erika", "Mustermann", null, null, 0)
        );
    }

    private void showContacts() {
        contactJFXListView.setItems(contacts);
        contactJFXListView.setCellFactory(contactListView -> new ContactListViewCell());
        contactJFXListView.setOnMouseClicked(this::onContactClicked);
        contactJFXListView.setExpanded(true);
    }

    private void onContactClicked(MouseEvent mouseEvent) {
        Contact contact = contactJFXListView.getSelectionModel().getSelectedItem();
        openChat(contact);
    }

    private void openChat(Contact contact) {
        int width = 500;
        int height = 800;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chat.fxml"));
            Parent root = loader.load();
            ChatController chatController = loader.getController();
            chatController.setContact(contact);
            Stage stage = new Stage();
            stage.setTitle(contact.getName());
            stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/icon256.png")));
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


    public void setData(String username, String vorname, String nachname) {
        this.username = username;
        this.vorname = vorname;
        this.nachname = nachname;
        nameLabel.setText(vorname + " " + nachname);
    }
}
