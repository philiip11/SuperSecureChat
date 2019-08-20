package SuperSecureChat;

import java.awt.*;
import java.awt.TrayIcon.MessageType;

public class TrayIconDemo {

    public static void test() throws AWTException {
        if (SystemTray.isSupported()) {
            TrayIconDemo td = new TrayIconDemo();
            td.displayTray();
        } else {
            System.err.println("System tray is not supported!");
        }
    }

    public void displayTray() throws AWTException {


        SystemTray tray = SystemTray.getSystemTray();


        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");

        //Image image = Toolkit.getDefaultToolkit().getImage("https://png.pngtree.com/png-clipart/20190617/original/pngtree-vector-chat-icon-png-image_3876650.jpg");


        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");

        trayIcon.setImageAutoSize(true);


        trayIcon.setToolTip("System tray icon demo");

        tray.add(trayIcon);


        trayIcon.displayMessage("SuperSecureChat", "application started", MessageType.INFO);


        //TODO every time a message get sent, notification //timer? --> abfragen bei nachrichtempfang
        //TODO own Icon for display message
        //TODO Symbol for chat? (tiny symbol in the symbols place )
    }


}