package SuperSecureChat.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.stage.Stage;

public class ShowPictureController {
    @FXML
    AnchorPane anchorPane;

    @FXML
    ImageView picture;
    private Stage stage;

    @FXML
    public void initialize() {
        anchorPane.setBackground(Background.EMPTY);
    }
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

    public void showPicture(Image image, double width, double height) {
        Platform.runLater(() -> {
            picture.setImage(image);
            anchorPane.setPrefWidth(width);
            anchorPane.setPrefHeight(height);
            picture.setFitWidth(width);
            picture.setFitHeight(height);
        });

    }

    public void close() {
        stage.close();

    }


}
