package SuperSecureChat.Controller;

import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Contacts.ContactList;
import SuperSecureChat.Contacts.ContactListViewCell;
import SuperSecureChat.Database;
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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;


public class ContactsController {

    @FXML
    JFXListView<Contact> contactJFXListView;
    @FXML
    Label nameLabel;
    @FXML
    ImageView myProfilePicture;

    private ContactList contactList = ContactList.getInstance();
    private static ContactsController INSTANCE = new ContactsController();
    private Stage stage;

    public void initialize() {
        //TODO Set KeyCombos

        new Thread(this::init).start();
        myProfilePicture.setImage(Contact.getMyContact().getJavaFXImage());
    }

    public static ContactsController getInstance() {
        return INSTANCE;
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
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> showContacts());
            }
        }, 0, 5000);


    }

    private void loadContacts() {
        //TODO Load Real Contacts from Database
        contactList.addAll(Database.getInstance().getContacts());
        contactList.remove(Contact.getMyContact());

    }

    private void showContacts() {
        contactJFXListView.refresh();
        contactJFXListView.setItems(contactList.getAllContacts());
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
            stage.getIcons().add(contact.getJavaFXImage());
            openNewStage(root, stage, width, height);
        } catch (Exception ignored) {

        }

    }


    void setData(String vorname, String nachname) {
        nameLabel.setText(vorname + " " + nachname);
    }

    public void editProfilePicture(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profilePicture.fxml"));
            Parent root = loader.load();
            ProfilePictureController profilePicture = loader.getController();
            Stage stage = new Stage();
            stage.setTitle("Profilbild ändern");
            stage.getIcons().add(Contact.getMyContact().getJavaFXImage());
            profilePicture.setStage(stage);
            profilePicture.setContactsController(this);
            openNewStage(root, stage, 500, 350);
        } catch (Exception ignored) {

        }
    }

    private void openNewStage(Parent root, Stage stage, int width, int height) {
        JFXDecorator decorator = new JFXDecorator(stage, root);
        decorator.setCustomMaximize(false);
        ImageView imageView = new ImageView(Contact.getMyContact().getJavaFXImage());
        imageView.setFitHeight(32);
        imageView.setFitWidth(32);
        decorator.setGraphic(imageView);
        Scene scene = new Scene(decorator, width, height, true, SceneAntialiasing.BALANCED);
        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(getClass().getResource("/css/jfoenix-fonts.css").toExternalForm(),
                getClass().getResource("/css/jfoenix-design.css").toExternalForm(),
                getClass().getResource("/css/custom.css").toExternalForm(),
                getClass().getResource("/css/jfoenix-main-demo.css").toExternalForm(),
                getClass().getResource("/css/super-secure-chat.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    public void updateProfilePicture() {
        System.out.println("Updating ProfilePicture");
        Platform.runLater(() -> myProfilePicture.setImage(Contact.getMyContact().getJavaFXImage()));
        //TODO Refresh Image somehow

    }

    public void setInstance(ContactsController contactsController) {
        INSTANCE = contactsController;
    }

    public void show() {
        Platform.runLater(() -> stage.show());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
