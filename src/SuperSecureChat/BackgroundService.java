package SuperSecureChat;

import SuperSecureChat.Network.UDPServer;

import java.awt.*;

public class BackgroundService implements Runnable {


    private static final BackgroundService INSTANCE = new BackgroundService();

    public static BackgroundService getInstance() {
        return INSTANCE;
    }

    @Override
    public void run() {
        startSystemTray();
        UDPServer.getInstance().run();

    }

    private void startSystemTray() {
        SystemTrayIcon systemTrayIcon = new SystemTrayIcon();
        try {
            systemTrayIcon.displayTray();                                 //Displays the message in the notificaton corner
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }


}
