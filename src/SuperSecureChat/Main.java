package SuperSecureChat;

import com.jfoenix.controls.JFXDecorator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import mslinks.ShellLink;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


public class Main extends Application {

    public static final String VERSION = "v0.1.6";

    //TODO Change Icon

    private static boolean file_put_contents(String filename, String data) {
        try {
            FileWriter fstream = new FileWriter(filename, true);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(data);
            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return (true);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        //Check for Updates:
        String path = Main.class.getResource("Main.class").toString();
        System.out.println(path);
        if (!(new File("PATH").exists())) {
            file_put_contents("PATH", path);
        }

        if (path.contains("update.jar")) {
            try {
                Thread.sleep(1000); // Wait for old process to close
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            File jar = new File("SuperSecureChat.jar");
            if (jar.exists()) {
                if (jar.delete()) {
                    System.out.println("SuperSecureChat.jar gelöscht");
                } else {
                    System.out.println("SuperSecureChat.jar konnte nicht gelöscht werden.");
                }
            } else {
                System.out.println("SuperSecureChat.jar konnte nicht gefunden werden.");
            }
            Files.copy(new File("update.jar").toPath(), jar.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Runtime.getRuntime().exec("java -jar SuperSecureChat.jar");
            System.exit(1);
        } else {
            File update = new File("update.jar");
            if (update.exists()) {
                if (update.delete()) {
                    System.out.println("Update gelöscht");
                } else {
                    System.out.println("Update konnte nicht gelöscht werden");
                }
            }
        }
        String pathToInk = System.getenv("appdata") + "\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\SuperSecureChat.lnk";
        File shortcut = new File(pathToInk);
        if (!shortcut.exists()) {
            if (!path.contains("IdeaProjects")) {
                ShellLink.createLink("SuperSecureChat.jar", pathToInk);
            }
        }

        startMain(primaryStage);

    }

    private void startMain(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));
        primaryStage.setTitle("SuperSecureChat");
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/icon2048.png")));
        JFXDecorator decorator = new JFXDecorator(primaryStage, root);
        decorator.setCustomMaximize(false);
        decorator.setGraphic(new ImageView(this.getClass().getResource("/icon16.png").toExternalForm()));
        Scene scene = new Scene(decorator, 600, 800, true, SceneAntialiasing.BALANCED);
        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(getClass().getResource("/css/jfoenix-fonts.css").toExternalForm(),
                getClass().getResource("/css/jfoenix-design.css").toExternalForm(),
                getClass().getResource("/css/custom.css").toExternalForm(),
                getClass().getResource("/css/jfoenix-main-demo.css").toExternalForm(),
                getClass().getResource("/css/super-secure-chat.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
        Platform.setImplicitExit(false);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void start() {
        launch();
    }
}
