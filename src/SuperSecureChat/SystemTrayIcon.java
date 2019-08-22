package SuperSecureChat;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class SystemTrayIcon {

    public static final String PATH_TO_TRAY_ICON = "/messengericon.jpg";
    private EventHandler<ActionEvent> onDismissedCallback, onShownCallback;

    public static void systemtraysupport() throws AWTException {
        if (SystemTray.isSupported()) {                                                     //frägt ab, ob SystemTray vom System unterstützt wird
            SystemTrayIcon sti = new SystemTrayIcon();
            sti.displayTray();
        } else {
            System.err.println("System tray is not supported on the current system!");      //ansonsten wirft es eine exception
            Platform.exit();
        }
    }


//    private void onShown(){
//        if(onShownCallback!= null)
//            onShownCallback.handle(new ActionEvent());
//    }
//
//
//    private void onDismissed(){
//        if(onDismissedCallback!=null)
//            onDismissedCallback.handle(new ActionEvent());
//    }


//---------------------------------------------

    public void displayTray() throws AWTException {

        SystemTray tray = SystemTray.getSystemTray();



        TrayIcon trayIcon = new TrayIcon(getTrayIcon(), "SuperSecureChat");                                 //symbol und text in der Windows Taskbar Status Area
        trayIcon.setImageAutoSize(true);
        tray.add(trayIcon);                                                                                         //displays the icon + notification
        trayIcon.displayMessage("SuperSecureChat", "application started", TrayIcon.MessageType.NONE); //popup text in the sidebar (on the right)


        //-------------------------------------


//        PopupMenu popmenu = new PopupMenu("Edit");
//        javafx.scene.control.MenuItem startapp = new javafx.scene.control.MenuItem("start");
//
//        trayIcon.setPopupMenu(popmenu);


        //startapp.setOnAction(event -> trayIcon.setPopupMenu(popmenu));







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
