package SuperSecureChat;

import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Crypto.Crypto;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
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

    public void displayTray() {

        //systemtraysupport();

        final PopupMenu popup = new PopupMenu();

        SystemTray tray = SystemTray.getSystemTray();

        trayIcon = new TrayIcon(getTrayIcon(), "SuperSecureChat");                                 //symbol und text in der Windows Taskbar Status Area
        trayIcon.setImageAutoSize(true);
        try {
            tray.add(trayIcon);                                                                                          //displays the icon + notification
        } catch (AWTException e) {
            e.printStackTrace();
        }
        //trayIcon.displayMessage("SuperSecureChat", "application started", TrayIcon.MessageType.NONE); //popup text in the sidebar (on the right)

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


        trayIcon.addActionListener(e -> {
            Platform.runLater(() -> ClassConnector.getInstance().openContacts());
        });
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(null,
                "Creators of the JavaFX application SuperSecureChat"));

        closeapp.addActionListener(e -> {
            Platform.runLater(() -> {
                SystemTray.getSystemTray().remove(trayIcon);
                com.sun.javafx.application.PlatformImpl.tkExit();
                Platform.exit();
                Runtime.getRuntime().halt(0);
            });
        });

        startapp.addActionListener(e -> {
            Platform.runLater(() -> ClassConnector.getInstance().openContacts());
            //trayIcon.addActionListener(event -> Platform.runLater(this::showStage);

            //setonStart();
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


