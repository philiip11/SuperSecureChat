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
        boolean noIpFoundException = true;
        for (Contact contact : contacts) {
            if (contact.getUrl() != null) {
                if (contact.getUrl().equals(ip)) {
                    contact.setLastOnline(Instant.now().getEpochSecond());
                    System.out.println("IP " + ip + " gehört " + contact.getId());
                    noIpFoundException = false;
                }
            }
        }
        if (noIpFoundException) {
            System.out.println("IP " + ip + " nicht gefunden!");
        }
    }

    public void addAll(ArrayList<Contact> contacts) {
        this.contacts.addAll(contacts);
    }
}
