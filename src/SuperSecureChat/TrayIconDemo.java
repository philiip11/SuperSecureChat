package SuperSecureChat;

import javafx.application.Platform;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.io.IOException;

public class TrayIconDemo {
    //public static final String PATH_TO_TRAY_ICON = "C:\\Users\\voelz\\Downloads\\messengericon.jpg"; // ARGH! Niemals absolute Pfade nutzen,
    // das läuft ja dann nur auf Sebastian's PC

    public static final String PATH_TO_TRAY_ICON = "/messengericon.jpg";
    public static void test() throws AWTException {
        if (SystemTray.isSupported()) {   //frägt ab ob SystemTray vom System unterstützt wird
            TrayIconDemo td = new TrayIconDemo();
            td.displayTray();
        } else {
            System.err.println("System tray is not supported on the current system!");  //ansonsten wirft es eine Fehlermeldung
            Platform.exit();
        }
    }

    public void displayTray() throws AWTException {


        SystemTray tray = SystemTray.getSystemTray();


        TrayIcon trayIcon = new TrayIcon(getTrayIcon(), "SuperSecureChat"); //tiny symbol (just text for now)
        trayIcon.setImageAutoSize(true);
        tray.add(trayIcon);  //displays the icon + notification
        trayIcon.displayMessage("SuperSecureChat", "application started", MessageType.NONE);  //popup text in the sidebar (on the right)


        //TODO every time a message get sent, notification //timer? --> abfragen bei nachrichtempfang  (catch received messages)
        //TODO own Icon for display message + Windows Taskbar Status Area icon
        //TODO Symbol for chat? (tiny symbol in the symbols place )
    }

    void clickontrayicon() throws AWTException {

        displayTray();

    }


//    private BufferedImage getTrayIcon() {
//        File f = new File(PATH_TO_TRAY_ICON);
//        BufferedImage bi = null;
//        try {
//            bi = ImageIO.read(f);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return bi;
//    }

    // Besser das Bild aus den Resourcen laden.

    private Image getTrayIcon() {
        try {
            return ImageIO.read(getClass().getResource(PATH_TO_TRAY_ICON));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}