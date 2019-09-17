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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;


public class Main extends Application {

    public static final String VERSION = "v0.2.6.2";

    private static final String PATH_TO_LNK = System.getenv("appdata") + "\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\SuperSecureChat.lnk";

    //TODO Change Icon
    //TODO Blockieren
    //TODO Freunde

    public static boolean file_put_contents(String filename, String data) {
        try {
            FileWriter fstream = new FileWriter(filename, true);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(data);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (true);
    }

    static void enableAutostart() {
        String path = Main.class.getResource("Main.class").toString();
        File shortcut = new File(PATH_TO_LNK);
        if (!shortcut.exists()) {
            if (!path.contains("IdeaProjects")) {
                try {
                    ShellLink.createLink("SuperSecureChat.jar", PATH_TO_LNK);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static void disableAutostart() {
        File shortcut = new File(PATH_TO_LNK);
        //noinspection ResultOfMethodCallIgnored
        shortcut.delete();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        //Check for Updates:
        String path = Main.class.getResource("Main.class").toString();
        System.out.println(path);
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
            Runtime.getRuntime().halt(0);
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
        if (!new File("config.prop").exists()) {

            enableAutostart();
            Properties properties = new Properties();
            properties.setProperty("autostart", "true");
            properties.store(new FileOutputStream("config.prop"), null);
        }
        //Font.loadFont(getClass().getResource("/emoji/NotoColorEmoji.ttf").toExternalForm(), 12);
        startMain(primaryStage);

    }

    public static void startMain(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(Main.class.getResource("/fxml/main.fxml"));
            primaryStage.setTitle("SuperSecureChat");
            primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/icon2048.png")));
            JFXDecorator decorator = new JFXDecorator(primaryStage, root);
            decorator.setCustomMaximize(false);
            decorator.setGraphic(new ImageView(Main.class.getResource("/icon16.png").toExternalForm()));
            Scene scene = new Scene(decorator, 600, 800, true, SceneAntialiasing.BALANCED);
            final ObservableList<String> stylesheets = scene.getStylesheets();
            stylesheets.addAll(Main.class.getResource("/css/jfoenix-fonts.css").toExternalForm(),
                    Main.class.getResource("/css/jfoenix-design.css").toExternalForm(),
                    Main.class.getResource("/css/custom.css").toExternalForm(),
                    Main.class.getResource("/css/jfoenix-main-demo.css").toExternalForm(),
                    Main.class.getResource("/css/super-secure-chat.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
            Platform.setImplicitExit(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }


}
