package SuperSecureChat.Controller;

import SuperSecureChat.ClassConnector;
import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Contacts.ContactList;
import SuperSecureChat.Contacts.ContactListViewCell;
import SuperSecureChat.NotificationHandler;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
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
        new Thread(this::init).start();
        myProfilePicture.setImage(Contact.getMyContact().getJavaFXImage());
        setData(Contact.getMyContact().getFirstname(), Contact.getMyContact().getLastname());
    }

    public static ContactsController getInstance() {
        return INSTANCE;
    }

    private void init() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> showContacts());
            }
        }, 0, 5000);
        /*if (Contact.getMyContact().getId().equals("Schneiderph")) {
            Platform.runLater(this::openNetworkStage);
        }*/

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

    public void openChat(Contact contact) {
        ChatController oldChatController = ClassConnector.getInstance().getChatController(contact);
        if (oldChatController != null) {
            if (oldChatController.isShowing()) {
                oldChatController.show();
                return;
            } else {
                ClassConnector.getInstance().removeChatController(oldChatController);
            }
        }
        int width = 500;
        int height = 800;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chat.fxml"));
            Stage stage = new Stage();
            Parent root = loader.load();
            NotificationHandler.getInstance().closeNotification(contact);
            ChatController chatController = loader.getController();
            chatController.setContact(contact);
            chatController.setStage(stage);
            stage.setTitle(contact.getName());
            stage.getIcons().add(contact.getJavaFXImage());
            stage.setY(0);
            openNewStage(root, stage, width, height, contact.getJavaFXImage());
        } catch (Exception ignored) {

        }
    }


    public void openNetworkStage() {
        Rectangle2D bounds = Screen.getScreens().get(Screen.getScreens().size() - 1).getVisualBounds();
        int width = (int) Math.min(bounds.getWidth(), 1280);
        int height = (int) Math.min(bounds.getHeight(), 1024);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/network.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Netzwerk");
            Image image = new Image(getClass().getResourceAsStream("/icons/round_import_export_white_48dp.png"));
            stage.getIcons().add(image);
            stage.setX(bounds.getMinX() + (bounds.getWidth() - width - 400) / 2);
            stage.setY(0);

            openNewStage(root, stage, width, height, image);
        } catch (Exception ignored) {

        }
    }


    public void setData(String vorname, String nachname) {
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
            openNewStage(root, stage, 500, 350, Contact.getMyContact().getJavaFXImage());
        } catch (Exception ignored) {

        }
    }

    private void openNewStage(Parent root, Stage stage, int width, int height, Image image) {
        JFXDecorator decorator = new JFXDecorator(stage, root);
        decorator.setCustomMaximize(false);
        ImageView imageView = new ImageView(image);
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

    void updateProfilePicture() {
        Platform.runLater(() -> {
            //myProfilePicture.setImage(new Image(new File(System.getenv("APPDATA") + "\\SuperSecureChat\\profile.png").toURI().toString()));
            myProfilePicture.setImage(Contact.getMyContact().getJavaFXImage());
        });

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
