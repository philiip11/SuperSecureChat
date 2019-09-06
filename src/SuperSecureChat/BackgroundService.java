package SuperSecureChat;

import SuperSecureChat.Network.Network;

public class BackgroundService implements Runnable {


    private static final BackgroundService INSTANCE = new BackgroundService();

    public static BackgroundService getInstance() {
        return INSTANCE;
    }

    private boolean running = false;

    @Override
    public void run() {
        if (!running) {
            new SystemTrayIcon().displayTray();
            Network network = Network.getInstance();
            network.initUDP();
            running = true;
        }

    }



}
