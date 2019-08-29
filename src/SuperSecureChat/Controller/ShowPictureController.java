package SuperSecureChat.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ShowPictureController {
    @FXML
    AnchorPane anchorPane;

    @FXML
    ImageView picture;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void showPicture(Image image) {
        Platform.runLater(() -> {
            picture.setImage(image);
            anchorPane.setPrefWidth(image.getWidth());
            anchorPane.setPrefHeight(image.getHeight());
        });

    }

    public void close() {
        stage.close();

    }


}
