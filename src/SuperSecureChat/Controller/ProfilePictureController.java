package SuperSecureChat.Controller;

import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Network.Network;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;


public class ProfilePictureController {

    @FXML
    Canvas canvas;

    private String filePath;
    private Stage stage;
    private ContactsController contactsController;

    @FXML
    public void initialize() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(Contact.getMyContact().getJavaFXImage(), 0, 0);
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
        gc.beginPath();
        gc.moveTo(0,128);
        gc.arc(128, 128, 128, 128, 180, 360);
        gc.closePath();
        gc.clip();
        gc.drawImage(image, x, y, width, height, 0, 0, 256, 256);
        gc.save();


    }

    public void saveProfilePicture() {
        System.out.println(System.getenv("APPDATA"));
        new File(System.getenv("APPDATA") + "\\SuperSecureChat").mkdir();
        File file = new File(System.getenv("APPDATA") + "\\SuperSecureChat\\profile.png");

        WritableImage writableImage = new WritableImage(256, 256);
        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);
        canvas.snapshot(sp, writableImage);
        RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
        try {
            ImageIO.write(renderedImage, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Contact.getMyContact().setImageFromFilePath(System.getenv("APPDATA") + "\\SuperSecureChat\\profile.png");
        Network.getInstance().updateContact();
        contactsController.updateProfilePicture();
        stage.close();
    }

    public void setContactsController(ContactsController contactsController) {
        this.contactsController = contactsController;
    }
}
