package SuperSecureChat;

import SuperSecureChat.Controller.ChatController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ClassConnector {


    private static final ClassConnector INSTANCE = new ClassConnector();
    private ObservableList<ChatController> chatControllers = FXCollections.observableArrayList();

    public static ClassConnector getInstance() {
        return INSTANCE;
    }

    public void addChatController(ChatController chatController) {
        chatControllers.add(chatController);
    }

    public void sendMessageToAllChatControllers(Message message) {
        for (ChatController chatController : chatControllers) {
            chatController.newMessage(message);
        }
    }
}
