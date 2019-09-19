package SuperSecureChat.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import emoji4j.Emoji;
import emoji4j.EmojiManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class EmojiPickerController {
    @FXML
    JFXTextField emojiTextField;
    @FXML
    TilePane tilePane;
    private Stage stage;
    private ChatController chatController;

    public void initialize() {
        //TODO Load Emojis
        new Thread(() -> {
            List<Emoji> emojis = EmojiManager.data();
            List<Node> buttons = new ArrayList<>();
            for (Emoji emoji : emojis) {

                JFXButton button = new JFXButton();
                button.setMaxHeight(32);
                button.setMaxWidth(32);
                button.setMinHeight(32);
                button.setMinWidth(32);
                button.setFont(Font.font(0));
                button.setGraphic(getImageViewFromEmoji(emoji.getEmoji()));
                button.setOnAction(actionEvent -> emojiButton(emoji.getEmoji()));
                button.getStyleClass().add("EmojiButton");
                buttons.add(button);
                Platform.runLater(() -> tilePane.getChildren().add(button));

            }
            //Platform.runLater(() -> masonryPane.getChildren().addAll(buttons));
        }).start();
    }

    private ImageView getImageViewFromEmoji(String string) {
        String emojiPath = "/emoji/128/emoji_u" + Integer.toHexString(string.codePointAt(0)) + ".png";
        try {
            ImageView emoji = new ImageView(getClass().getResource(emojiPath).toExternalForm());
            emoji.setFitHeight(32);
            emoji.setFitWidth(32);
            return emoji;
        } catch (NullPointerException e) {
            return null;
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public ChatController getChatController() {
        return chatController;
    }

    void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }

    public void close() {
        stage.close();
    }

    public void buttonClick() {
        chatController.add(emojiTextField.getText());
    }

    public void txtOnKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            buttonClick();
            emojiTextField.clear();
        }
    }

    public void emojiButton(String emoji) {
        chatController.add(emoji);

    }
}
