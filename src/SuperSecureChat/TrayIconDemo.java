package SuperSecureChat;

import javafx.application.Platform;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TrayIconDemo {
    public static final String PATH_TO_TRAY_ICON = "C:\\Users\\voelz\\Downloads\\Telefon.png";
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

        PopupMenu popupMenu = new PopupMenu();
        MenuItem exitItem = new MenuItem("exit");
        popupMenu.add(exitItem);


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

    private BufferedImage getTrayIcon() {
        File f = new File(PATH_TO_TRAY_ICON);
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bi;
    }
}