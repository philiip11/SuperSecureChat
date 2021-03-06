package SuperSecureChat;

import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Controller.NotificationController;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationHandler {
    private static final NotificationHandler INSTANCE = new NotificationHandler();
    private CopyOnWriteArrayList<NotificationController> notificationControllers = new CopyOnWriteArrayList<>();
    private Contact me = Contact.getMyContact();

    public static NotificationHandler getInstance() {
        return INSTANCE;
    }

    void newMessage(Message message) {
        Contact otherContact = me;
        if (message.getSender() != null) {
            otherContact = message.getSender().getId().equals(me.getId()) ?
                    message.getReceiver() : message.getSender();
        }
        if (message.getData().equals("DELDATA:THIS")) {
            return;
        }
        boolean success = false;
        if (message.getReceiver() != null && message.getSender() != null) {
            for (NotificationController notificationController : notificationControllers) {
                if (notificationController.getContact().getId().equals(otherContact.getId())) {
                    if ((message.getReceiver().getId().equals(me.getId()) &&            // Nachricht von Kontakt an mch
                            message.getSender().getId().equals(otherContact.getId())) ||
                            (message.getReceiver().getId().equals(otherContact.getId()) &&   // Nachricht von mich an Kontakt
                                    message.getSender().getId().equals(me.getId()))) {
                        notificationController.newMessage(message);
                        success = true;
                    }
                }
            }
        }
        if (!success) {
            Contact finalOtherContact = otherContact;
            Platform.runLater(() -> openNotificationWindow(finalOtherContact, message));
        }
    }

    public void closeNotification(Contact contact) {
        for (NotificationController notificationController : notificationControllers) {
            if (contact.getId().equals(notificationController.getContact().getId())) {
                notificationController.close();
            }
        }
    }

    private void openNotificationWindow(Contact otherContact, Message message) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/notification.fxml"));
            Stage stage = new Stage();
            Parent root = loader.load();
            int width = 350;
            int height = 300;
            NotificationController notificationController = loader.getController();
            notificationController.setContact(otherContact);
            notificationController.setStage(stage);
            notificationController.newMessage(message);
            notificationControllers.add(notificationController);
            stage.getIcons().add(otherContact.getJavaFXImage());

            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            stage.setX(bounds.getMaxX() - width - 12);
            stage.setY(bounds.getMaxY() - height - 12);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setAlwaysOnTop(true);
            openNewStage(root, stage, width, height, otherContact.getJavaFXImage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void openNewStage(Parent root, Stage stage, int width, int height, Image image) {

        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(32);
        imageView.setFitWidth(32);
        Scene scene = new Scene(root, width, height, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.TRANSPARENT);
        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(getClass().getResource("/css/jfoenix-fonts.css").toExternalForm(),
                getClass().getResource("/css/jfoenix-design.css").toExternalForm(),
                getClass().getResource("/css/custom.css").toExternalForm(),
                getClass().getResource("/css/jfoenix-main-demo.css").toExternalForm(),
                getClass().getResource("/css/super-secure-chat.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    public void notificationControllerClosed(NotificationController notificationController) {
        notificationControllers.remove(notificationController);
    }
}
