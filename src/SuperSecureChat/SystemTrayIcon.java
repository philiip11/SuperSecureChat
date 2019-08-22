package SuperSecureChat;

import javafx.application.Platform;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class SystemTrayIcon {

    public static final String PATH_TO_TRAY_ICON = "/messengericon.jpg";

    public static void systemtraysupport() throws AWTException {
        if (SystemTray.isSupported()) {                                                     //frägt ab, ob SystemTray vom System unterstützt wird
            SystemTrayIcon sti = new SystemTrayIcon();
            sti.displayTray();
        } else {
            System.err.println("System tray is not supported on the current system!");      //ansonsten wirft es eine exception
            Platform.exit();
        }
    }


    public void displayTray() throws AWTException {

        final PopupMenu popup = new PopupMenu();


        SystemTray tray = SystemTray.getSystemTray();


        TrayIcon trayIcon = new TrayIcon(getTrayIcon(), "SuperSecureChat");                                 //symbol und text in der Windows Taskbar Status Area
        trayIcon.setImageAutoSize(true);
        tray.add(trayIcon);                                    //displays the icon + notification
        trayIcon.displayMessage("SuperSecureChat", "application started", TrayIcon.MessageType.NONE); //popup text in the sidebar (on the right)

//Creating some stuff --> still under construction
        MenuItem aboutItem = new MenuItem("Info");
        CheckboxMenuItem cb1 = new CheckboxMenuItem("Test");
        CheckboxMenuItem cb2 = new CheckboxMenuItem("test_2");
        Menu displayMenu = new Menu("Settings");
        MenuItem startapp = new MenuItem("start");
        MenuItem closeapp = new MenuItem("close");

        popup.add(aboutItem);
        popup.addSeparator();
        popup.add(cb1);
        popup.add(cb2);
        popup.addSeparator();
        popup.add(displayMenu);
        displayMenu.add(startapp);
        displayMenu.add(closeapp);
        //popup.add(closeapp);

        trayIcon.setPopupMenu(popup);


//        PopupMenu popmenu = new PopupMenu("Edit");
//        javafx.scene.control.MenuItem startapp = new javafx.scene.control.MenuItem("start");
//        trayIcon.setPopupMenu(popmenu);
//
//        MenuItem startapp = new MenuItem("start");
//        startapp.setOnAction(event -> {
//
//        });
//        trayIcon.setPopupMenu(popmenu);


    }


    //TODO Methode um Benachrichtigungen anzuzeigen

    public void showNotification(Image image, String title, String message, Runnable onClick) {
        //TODO
    }


    private Image getTrayIcon() {
        try {
            return ImageIO.read(getClass().getResource(PATH_TO_TRAY_ICON));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
