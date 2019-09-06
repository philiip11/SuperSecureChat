package SuperSecureChat;

import SuperSecureChat.Controller.ChatController;
import SuperSecureChat.Controller.NetworkController;
import com.jfoenix.controls.JFXDecorator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ClassConnector {


    private static final ClassConnector INSTANCE = new ClassConnector();
    private ObservableList<ChatController> chatControllers = FXCollections.observableArrayList();
    private SystemTrayIcon systemTrayIcon;
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
        sendMessageToNetworkMap(message);
    }

    public void sendMessageToNetworkMap(Message message) {
        if (networkController != null) {
            networkController.newMessage(message);
        }
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
            stage.requestFocus();
            stage.toFront();
        } catch (Exception ignored) {

        }
    }

}
