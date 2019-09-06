package SuperSecureChat;

import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Crypto.Crypto;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class SystemTrayIcon {

    private FXMLLoader Loader;


    public static final String PATH_TO_TRAY_ICON = "/icon2048.png";

    private TrayIcon trayIcon;


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

        //systemtraysupport();

        final PopupMenu popup = new PopupMenu();

        SystemTray tray = SystemTray.getSystemTray();

        trayIcon = new TrayIcon(getTrayIcon(), "SuperSecureChat");                                 //symbol und text in der Windows Taskbar Status Area
        trayIcon.setImageAutoSize(true);
        tray.add(trayIcon);                                                                                          //displays the icon + notification
        trayIcon.displayMessage("SuperSecureChat", "application started", TrayIcon.MessageType.NONE); //popup text in the sidebar (on the right)

        // Creating some stuff --> still under construction

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("config.prop"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        MenuItem aboutItem = new MenuItem("Info");
        CheckboxMenuItem autostartItem = new CheckboxMenuItem("Autostart", properties.getProperty("autostart").equals("true"));
        Menu settingsMenu = new Menu("Settings");
        MenuItem startapp = new MenuItem("start");
        MenuItem closeapp = new MenuItem("close");

        popup.add(startapp);
        popup.addSeparator();
        popup.add(settingsMenu);
        settingsMenu.add(autostartItem);
        settingsMenu.add(aboutItem);
        popup.addSeparator();
        popup.add(closeapp);
        trayIcon.setPopupMenu(popup);


        trayIcon.addActionListener(new ActionListener() {               //TODO soll Anwendung maximieren //doppelclick *thinking*
            public void actionPerformed(ActionEvent e) {
                ClassConnector.getInstance().openContacts(); //TODO Funktioniert so leider nicht, hat jemand eine bessere Idee? :-)
                JOptionPane.showMessageDialog(null,
                        "[Open Application] Hopefully coming soon");

            }
        });
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "Creators of the JavaFX application SuperSecureChat");
            }
        });

        closeapp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                //SystemTray.getSystemTray().remove(trayIcon);
                Platform.exit();
                System.exit(1);
            }
        });

        startapp.addActionListener(new ActionListener() {       //TODO soll Anwendung starten *thinking*
            @Override
            public void actionPerformed(ActionEvent e) {
                trayIcon.displayMessage("Starting Application", "loading assets", TrayIcon.MessageType.NONE);
                //Application.launch();
                //trayIcon.addActionListener(event -> Platform.runLater(this::showStage);

                //setonStart();
            }
        });     //ActionListener zu ende  // Klammern *grrr*


        autostartItem.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                System.out.println("Autostart = " + autostartItem.getState());
                if (autostartItem.getState()) {
                    properties.setProperty("autostart", "true");
                    Main.enableAutostart();
                } else {
                    properties.setProperty("autostart", "false");
                    Main.disableAutostart();
                }
                try {
                    properties.store(new FileOutputStream("config.prop"), null);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            tray.remove(trayIcon);
            System.out.println("Removed TrayIcon");
        }));

        ClassConnector.getInstance().addSystemTrayIcon(this);


    }


    public void showNotification(Image image, String title, String message, Runnable onClick) {
        trayIcon.displayMessage(title, message, TrayIcon.MessageType.NONE);


    }


    private Image getTrayIcon() {
        try {
            return ImageIO.read(getClass().getResource(PATH_TO_TRAY_ICON));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void showMessage(Message message) {
        if (message.getReceiver().getId().equals(Contact.getMyContact().getId())) {
            Crypto crypto = new Crypto();
            crypto.setSecretKey(Database.getInstance().getSecretKeyByContact(message.getSender()));
            String text = crypto.decrypt(message.getText());
            trayIcon.displayMessage(message.getSender().getName(), text, TrayIcon.MessageType.NONE);
        }

    }

    public void setonStart() {
        Loader = new FXMLLoader(getClass().getResource("/fxml/contacts.fxml"));   //fx:controller="SuperSecureChat.Controller.ContactsController"
        Loader.setController(this);
        try {
            Loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //fx:controller="SuperSecureChat.Controller.ContactsController"
    }


}


