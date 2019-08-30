package SuperSecureChat.Controller;

import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;

public class ChatPictureController {

    @FXML
    Canvas canvas;

    private Stage stage;
    private ChatController chatController;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void openFileDialog() {   //TODO richtig so?
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Bilder", "*.jpg", "*.png", "*.gif", "*.webp", "*.tiff", "*.psd",
                "*.raw", "*.bmp", "*.heif", "*.indd", "*.jpeg", "*.jpe", "*.jif", "*.jfif", "*.jfi")
        );

        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            return;
        }

    }


    public void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }

}
