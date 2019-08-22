package SuperSecureChat;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        final FXMLLoader mLLoader;

        SystemTray tray = SystemTray.getSystemTray();


        TrayIcon trayIcon = new TrayIcon(getTrayIcon(), "SuperSecureChat");                                 //symbol und text in der Windows Taskbar Status Area
        trayIcon.setImageAutoSize(true);
        tray.add(trayIcon);                                    //displays the icon + notification
        trayIcon.displayMessage("SuperSecureChat", "application started", TrayIcon.MessageType.NONE); //popup text in the sidebar (on the right)

//Creating some stuff --> still under construction
        MenuItem aboutItem = new MenuItem("Info");
        Menu displayMenu = new Menu("Settings");
        MenuItem startapp = new MenuItem("start");
        MenuItem closeapp = new MenuItem("close");
        //CheckboxMenuItem cbmi2 = new CheckboxMenuItem("test_2");
        //CheckboxMenuItem cbmi1 = new CheckboxMenuItem("Test");

        //popup.add(cbmi1);
        //popup.add(cbmi2);
        popup.add(startapp);
        popup.add(closeapp);
        popup.addSeparator();
        popup.add(displayMenu);
        popup.addSeparator();
        displayMenu.add(aboutItem);
        trayIcon.setPopupMenu(popup);


        trayIcon.addActionListener(new ActionListener() {               //TODO soll Anwendung maximieren //doppelclick *thinking*
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "[Open Application] Hopefully coming soon ;)");
            }
        });
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "Creators of the JavaFX application SuperSecureChat");
            }
        });

        closeapp.addActionListener(new ActionListener() {           //DONE schließt das Programm über den Menupoint "Close"
            public void actionPerformed(ActionEvent e) {
                //SystemTray.getSystemTray().remove(trayIcon);
                System.exit(0);
            }
        });

        startapp.addActionListener(new ActionListener() {       //TODO soll Anwendung starten *thinking*
            @Override
            public void actionPerformed(ActionEvent e) {


            }
        });

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
