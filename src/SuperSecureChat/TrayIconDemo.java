package SuperSecureChat;

import java.awt.*;
import java.awt.TrayIcon.MessageType;

public class TrayIconDemo {

    public static void test() throws AWTException {
        if (SystemTray.isSupported()) {
            TrayIconDemo td = new TrayIconDemo();
            td.displayTray();
        } else {
            System.err.println("System tray not supported!");
        }
    }

    public void displayTray() throws AWTException {


        SystemTray tray = SystemTray.getSystemTray();


        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");

        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");

        trayIcon.setImageAutoSize(true);

        trayIcon.setToolTip("System tray icon demo");
        tray.add(trayIcon);

        trayIcon.displayMessage("Test_Notification_Header", "notification demo message", MessageType.INFO);
    }
}