package SuperSecureChat;

import javafx.application.Platform;

import java.awt.*;
import java.awt.TrayIcon.MessageType;

public class TrayIconDemo {

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




        //Image image = Toolkit.getDefaultToolkit().getImage("https://png.pngtree.com/png-clipart/20190617/original/pngtree-vector-chat-icon-png-image_3876650.jpg");


        Image image = Toolkit.getDefaultToolkit().createImage("icon64.png");


        TrayIcon trayIcon = new TrayIcon(image, "SuperSecureChat"); //tiny symbol (just text for now)
        trayIcon.setImageAutoSize(true);
        //trayIcon.setImage(image);


        //trayIcon.setToolTip("System tray icon");

        tray.add(trayIcon);

        String imagesetter = "https://png.pngtree.com/png-clipart/20190617/original/pngtree-vector-chat-icon-png-image_3876650.jpg";


        trayIcon.displayMessage("SuperSecureChat", "application started", MessageType.INFO);


        //TODO every time a message get sent, notification //timer? --> abfragen bei nachrichtempfang
        //TODO own Icon for display message
        //TODO Symbol for chat? (tiny symbol in the symbols place )
    }


}