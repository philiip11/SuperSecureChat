package SuperSecureChat.Controller;

import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;


public class ProfilePictureController {

    @FXML
    ImageView profilePictureImageView;

    private String filePath;
    private Stage stage;

    @FXML
    public void initialize() {
        //TODO GET OLD Profile Picture
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void openFileDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Bilder (JPG/PNG)", "*.jpg", "*.jpeg", "*.jfif", "*.png")
        );

        File file = fileChooser.showOpenDialog(stage);
        Image image = new Image(file.toURI().toString());
        Canvas canvas = new Canvas(256, 256);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double x = 0;
        double y = 0;
        double width = 0;
        double height = 0;
        if (image.getWidth() > image.getHeight()) {
            // Landscape
            height = image.getHeight();
            width = height;
            y = 0;
            x = (image.getWidth() - width) / 2;
        } else {
            width = image.getWidth();
            height = width;
            y = (image.getHeight() - height) / 2;
            x = 0;
        }
        gc.setFill(Color.AQUAMARINE);
        gc.fillRect(0, 0, 256, 256);
        /*gc.beginPath();
        gc.moveTo(0,128);
        gc.arc(128,128,128,128,180,260);
        gc.closePath();
        gc.clip();*/
        //gc.drawImage(image, x, y, width, height,0, 0, 256, 256);
        gc.save();
        WritableImage prev = new WritableImage(256, 256);
        prev = canvas.snapshot(new SnapshotParameters(), prev);


        profilePictureImageView.setImage(prev);
    }

    public void saveProfilePicture() {

    }
}
