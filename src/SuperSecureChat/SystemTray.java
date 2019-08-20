package SuperSecureChat;

import java.awt.*;

public class SystemTray {



    public static void main(String[] args) throws AWTException {
        if (java.awt.SystemTray.isSupported()) {
            SystemTray td = new SystemTray();
            td.displayTray();
        } else {
            System.err.println("System tray not supported!");
        }
    }


    public void displayTray() throws AWTException {
        //Obtain only one instance of the SystemTray object
        java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();

        //If the icon is a file
        //Image image = Toolkit.getDefaultToolkit().createImage("/icon.png");
        //Alternative (if the icon is on the classpath):
        Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/icon.png"));

        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
        //Let the system resize the image if needed
        trayIcon.setImageAutoSize(true);
        //Set tooltip text for the tray icon
        trayIcon.setToolTip("System tray icon demo");
        tray.add(trayIcon);

        trayIcon.displayMessage("Header Demo Test", "notification demo", TrayIcon.MessageType.INFO);
    }           //"It is working, isn't it" -->  Front text in the notification header     //"notification demo" --> the text under the notification
}
