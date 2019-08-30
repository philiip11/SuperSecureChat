package SuperSecureChat.Controller;

import SuperSecureChat.BackgroundService;
import SuperSecureChat.ClassConnector;
import SuperSecureChat.Contacts.Contact;
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

    // TODO Implement Chat

    // TODO Implement GUI via JavaFX
    // TODO Find Users via TCP
    // TODO Encrypt Messages
    // TODO Send Messages via TCP to Every User
    // TODO Filter Messages send to me
    // TODO Save Messages in Database
    // TODO Ping for Online-Status
    // TODO Status und Profilbild
    // TODO Windows-Notifications
    // TODO Add DataMessages

    // TODO Show Network Graphically


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
    //TODO Label einbinden und erstellen.

    private String vorname;
    private String nachname;

    public void initialize() {
        //TODO Set KeyCombos
        daytimeLabel.setText(gettimebycalendar());
        GridPane.setHalignment(welcomeLabel, HPos.CENTER);
        GridPane.setHalignment(daytimeLabel, HPos.CENTER);
        GridPane.setHalignment(usernameLabel, HPos.CENTER);
        //TODO good morning einbinden

        new Thread(this::init).start(); // Daten asynchron laden
    }

    private void init() {
        String fullName = getFullName();
        String[] nameArr = fullName.split(",");
        vorname = nameArr[1].trim();
        nachname = nameArr[0].trim();
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


        //TODO Datenbank laden
        new Thread(() -> {
            BackgroundService backgroundService = BackgroundService.getInstance();
            backgroundService.run();
        }).start();
        Platform.runLater(this::openContacts);
        Platform.runLater(this::close);
    }

    private void checkForUpdate() {
        HttpsURLConnection con = null;
        try {
            URL url = new URL("https://api.github.com/repos/philiip11/SuperSecureChat/releases");
            con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            if (status == 200) {
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

                if (!newVersion.equals(Main.VERSION)) {
                    System.out.println(newVersion + " != " + Main.VERSION);
                    Platform.runLater(() -> {
                        welcomeLabel.setText("Update wird heruntergeladen...");
                        daytimeLabel.setText(newVersionName);
                    });
                    JSONArray assets = (JSONArray) release.get("assets");
                    JSONObject asset;
                    String fileName;
                    int i = -1;
                    do {
                        i++;
                        asset = (JSONObject) assets.get(i);
                        fileName = (String) asset.get("name");
                    } while (!fileName.equals("SuperSecureChat.jar"));

                    Platform.runLater(() -> progress.setVisible(true));
                    Platform.runLater(() -> spinner.setVisible(false));
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
                        int x = 0;
                        while ((x = in.read(data, 0, 1024)) >= 0) {
                            downloadedFileSize += x;
                            final double currentProgress = (double) downloadedFileSize / (double) completeFileSize;
                            Platform.runLater(() -> progress.setProgress(currentProgress));

                            bout.write(data, 0, x);
                        }
                        bout.close();
                        in.close();
                        Runtime.getRuntime().exec("java -jar update.jar");
                        System.exit(1);
                    } catch (IOException ignored) {
                    }


                    //FileUtils.copyURLToFile(new URL((String) asset.get("browser_download_url")), new File("update.jar"));

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


//TODO Begrüßung nach Tageszeit

    public String gettimebycalendar() {
        Calendar calendar = Calendar.getInstance();
        int timeofday = calendar.get(Calendar.HOUR_OF_DAY);

        if (timeofday < 12) {
            return "Good Morning";
        } else if (timeofday < 16) {
            return "Good Afternoon";
        } else if (timeofday < 21) {
            return "Good Evening";
        } else {
            return "Good Night";
        }

    }


}
