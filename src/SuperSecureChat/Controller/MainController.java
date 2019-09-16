package SuperSecureChat.Controller;

import SuperSecureChat.BackgroundService;
import SuperSecureChat.ClassConnector;
import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Database;
import SuperSecureChat.Main;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXSpinner;
import com.sun.jna.platform.win32.Secur32;
import com.sun.jna.ptr.IntByReference;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.Calendar;

public class MainController {

    // DONE Implement Chat

    // DONE Implement GUI via JavaFX
    // DONE Find Users via UDP
    // DONE Encrypt Messages
    // DONE Send Messages via TCP to Every User
    // DONE Filter Messages send to me
    // DONE Save Messages in Database
    // DONE Ping for Online-Status
    // TODO Status und Profilbild
    // TODO Windows-Notifications



    @FXML
    Label usernameLabel;
    @FXML
    Label daytimeLabel;
    @FXML
    Label welcomeLabel;
    @FXML
    GridPane gridPane;
    @FXML
    JFXProgressBar progress;
    @FXML
    JFXSpinner spinner;
    @FXML
    Text changelog;


    public void initialize() {
        daytimeLabel.setText(gettimebycalendar());
        GridPane.setHalignment(welcomeLabel, HPos.CENTER);
        GridPane.setHalignment(daytimeLabel, HPos.CENTER);
        GridPane.setHalignment(usernameLabel, HPos.CENTER);

        new Thread(this::init).start(); // Daten asynchron laden
    }

    private void init() {
        String fullName = getFullName();
        String[] nameArr = fullName.split(",");
        String vorname = nameArr[1].trim();
        String nachname = nameArr[0].trim();
        Platform.runLater(() -> usernameLabel.setText(vorname));
        String username = new com.sun.security.auth.module.NTSystem().getName();
        Contact.setMyName(username, vorname, nachname);
        /*try (OutputStream output = new FileOutputStream("config.properties")) {
            Properties prop = new Properties();
            username = new com.sun.security.auth.module.NTSystem().getName(); // Nutzername
            System.out.println(username);
            prop.setProperty("username", username);
            prop.setProperty("vorname", vorname);
            prop.setProperty("nachname", nachname);
            System.out.println(fullName);
            Platform.runLater(() -> {
                usernameLabel.setText(vorname);
            });
            prop.store(output, "");
        } catch (IOException io) {
            io.printStackTrace();
        }*/

        checkForUpdate();

        new Thread(() -> {
            //noinspection ResultOfMethodCallIgnored
            Database.getInstance();
            BackgroundService backgroundService = BackgroundService.getInstance();
            backgroundService.run();
        }).start();
        Platform.runLater(this::openContacts);
        Platform.runLater(this::close);
    }

    public void checkForUpdate() {
        System.out.println("Check for Update");
        HttpsURLConnection con = null;
        try {
            URL url = new URL("https://api.github.com/repos/philiip11/SuperSecureChat/releases");
            con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            if (status == 200) {
                System.out.println("Response from GitHub: 200 OK");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line;
                StringBuilder content = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    content.append(line);
                }
                JSONArray jsonArray = (JSONArray) new JSONParser().parse(content.toString());
                JSONObject release = (JSONObject) jsonArray.get(0);
                String newVersion = (String) release.get("tag_name");
                String newVersionName = (String) release.get("name");
                String newVersionBody = (String) release.get("body");

                if (!newVersion.equals(Main.VERSION)) {
                    System.out.println(newVersion + " != " + Main.VERSION);
                    if (welcomeLabel != null) {
                        Platform.runLater(() -> {
                            welcomeLabel.setText("Update wird heruntergeladen...");
                            daytimeLabel.setText(newVersionName);
                            changelog.setText(newVersionBody);
                            usernameLabel.setVisible(false);
                            changelog.setVisible(true);

                        });
                    }
                    JSONArray assets = (JSONArray) release.get("assets");
                    JSONObject asset;
                    String fileName;
                    int i = -1;
                    do {
                        i++;
                        asset = (JSONObject) assets.get(i);
                        fileName = (String) asset.get("name");
                    } while (!fileName.equals("SuperSecureChat.jar"));

                    if (welcomeLabel != null) {
                        Platform.runLater(() -> progress.setVisible(true));
                        Platform.runLater(() -> spinner.setVisible(false));
                    }
                    JSONObject finalAsset = asset;
                    try {
                        URL url1 = new URL((String) finalAsset.get("browser_download_url"));
                        HttpsURLConnection httpConnection = (HttpsURLConnection) (url1.openConnection());
                        long completeFileSize = httpConnection.getContentLength();
                        BufferedInputStream in = new BufferedInputStream(httpConnection.getInputStream());
                        FileOutputStream fos = new FileOutputStream("update.jar");
                        BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
                        byte[] data = new byte[1024];
                        long downloadedFileSize = 0;
                        int x;
                        while ((x = in.read(data, 0, 1024)) >= 0) {
                            downloadedFileSize += x;
                            final double currentProgress = (double) downloadedFileSize / (double) completeFileSize;
                            if (progress != null) {
                                Platform.runLater(() -> progress.setProgress(currentProgress));
                            }
                            bout.write(data, 0, x);
                        }
                        bout.close();
                        in.close();
                        Runtime.getRuntime().exec("java -jar update.jar");
                        Platform.runLater(() -> {
                            com.sun.javafx.application.PlatformImpl.tkExit();
                            Platform.exit();
                            Runtime.getRuntime().halt(0);
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    private void close() {
        Stage stage = (Stage) usernameLabel.getScene().getWindow();
        stage.close();
    }

    private void openContacts() {
        ClassConnector.getInstance().openContacts();
    }

    private String getFullName() {
        char[] name = new char[256];
        Secur32.INSTANCE.GetUserNameEx(
                Secur32.EXTENDED_NAME_FORMAT.NameDisplay,
                name,
                new IntByReference(name.length)
        );
        return new String(name).trim();
    }

    private String gettimebycalendar() {
        Calendar calendar = Calendar.getInstance();
        int timeofday = calendar.get(Calendar.HOUR_OF_DAY);

        if (timeofday < 8) {
            return "Guten Morgen Frühaufsteher ;-)";
        } else if (timeofday < 11) {
            return "Guten Morgen";
        } else if (timeofday < 12) {
            return "Mahlzeit!";
        } else if (timeofday < 16) {
            return "Guten Nachmittag";
        } else if (timeofday < 21) {
            return "Guten Abend";
        } else {
            return "Gute Nacht";
        }

    }


}
