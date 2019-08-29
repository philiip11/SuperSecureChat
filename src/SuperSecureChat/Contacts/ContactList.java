package SuperSecureChat.Contacts;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.Instant;
import java.util.ArrayList;

public class ContactList {

    private static final ContactList INSTANCE = new ContactList();
    private ObservableList<Contact> contacts = FXCollections.observableArrayList();

    public static ContactList getInstance() {
        return INSTANCE;
    }

    public void addContact(Contact contact) {
        Platform.runLater(() -> removeContact(contact.getId()));
        Platform.runLater(() -> contacts.add(contact));

    }

    private void removeContact(String id) {
        contacts.removeIf(c -> c.getId().equals(id));
    }

    public ObservableList<Contact> getAllContacts() {
        return contacts;
    }


    public void setOnlineByIp(String ip) {
        while (true) {
            for (Contact contact : contacts) {
                if (contact.getUrl() != null) {
                    if (contact.getUrl().equals(ip)) {
                        contact.setLastOnline(Instant.now().getEpochSecond());
                        System.out.println("IP " + ip + " gehört " + contact.getId());
                        return;
                    }
                }
            }
            try {
                System.out.println("sleep");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void addAll(ArrayList<Contact> contacts) {
        this.contacts.addAll(contacts);
    }

    public void remove(Contact contact) {
        for (Contact c : contacts) {
            if (c.getId().equals(contact.getId())) {
                contacts.remove(c);
                return;
            }
        }
    }

    public Contact getContactByIP(String ip) {
        for (Contact contact : contacts) {
            if (contact.getUrl() != null) {
                if (contact.getUrl().equals(ip)) {
                    return contact;
                }
            }
        }

        return null;
    }
}
