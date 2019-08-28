package SuperSecureChat;

import SuperSecureChat.Network.Network;

import java.awt.*;

public class BackgroundService implements Runnable {


    private static final BackgroundService INSTANCE = new BackgroundService();

    public static BackgroundService getInstance() {
        return INSTANCE;
    }

    @Override
    public void run() {
        starttogetTime();
        startSystemTray();
        Network network = Network.getInstance();
        network.initUDP();


    }

    private void startSystemTray() {
        SystemTrayIcon systemTrayIcon = new SystemTrayIcon();
        try {
            systemTrayIcon.displayTray();                                 //Displays the message in the notificaton corner
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private void starttogetTime() {
        GreetingsAfterTime greetingsAfterTime = new GreetingsAfterTime();
        greetingsAfterTime.gettimebycalendar();
    }


}
