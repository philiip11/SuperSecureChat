package SuperSecureChat;

import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Controller.ChatController;
import SuperSecureChat.Controller.NetworkController;
import SuperSecureChat.NetworkMap.NetworkContactMessage;
import SuperSecureChat.NetworkMap.NetworkIconMessage;
import SuperSecureChat.NetworkMap.NetworkMessage;
import com.jfoenix.controls.JFXDecorator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ClassConnector {


    private static final ClassConnector INSTANCE = new ClassConnector();
    private ObservableList<ChatController> chatControllers = FXCollections.observableArrayList();
    private SystemTrayIcon systemTrayIcon;

    public NetworkController getNetworkController() {
        return networkController;
    }

    private NetworkController networkController;

    public static ClassConnector getInstance() {
        return INSTANCE;
    }

    public void addChatController(ChatController chatController) {
        chatControllers.add(chatController);
    }

    public void addNetworkController(NetworkController networkController) {
        this.networkController = networkController;
    }

    void addSystemTrayIcon(SystemTrayIcon systemTrayIcon) {
        this.systemTrayIcon = systemTrayIcon;

    }

    public void sendMessageToAllChatControllers(Message message, boolean notification) {
        for (ChatController chatController : chatControllers) {
            chatController.newMessage(message);
        }
        if (notification) {
            systemTrayIcon.showMessage(message);
        }
    }

    public NetworkMessage sendMessageToNetworkMap(Message message, Message m) {
        if (networkController != null) {
            return networkController.newMessage(message, m);
        }
        return null;
    }

    public NetworkContactMessage sendContactToNetworkMap(Contact contact, Message message) {
        if (networkController != null) {
            return networkController.newContact(contact, message);
        }
        return null;
    }

    public NetworkIconMessage sendIconMessageToNetworkMap(Image image, Message message) {
        if (networkController != null) {
            return networkController.newIconMessage(image, message);
        }

        return null;
    }

    public NetworkMessage sendNetworkMessageToNetworkMap(NetworkMessage message) {
        if (networkController != null) {
            return networkController.newNetworkMessage(message);
        }
        return message;
    }

    public void openContacts() {
        int width = 400;
        int height = 800;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/contacts.fxml"));

            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("SSC - Kontakte");
            stage.getIcons().add(new javafx.scene.image.Image(this.getClass().getResourceAsStream("/icon2048.png")));

            Rectangle2D bounds = Screen.getScreens().get(Screen.getScreens().size() - 1).getVisualBounds();
            stage.setX(bounds.getMaxX() - width);
            stage.setY(bounds.getMinY() + (bounds.getHeight() - height) / 2);

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
            stage.requestFocus();
            stage.toFront();
        } catch (Exception ignored) {

        }
    }

}
