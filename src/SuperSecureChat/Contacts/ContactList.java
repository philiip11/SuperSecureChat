package SuperSecureChat.Contacts;

import SuperSecureChat.ClassConnector;
import SuperSecureChat.Database;
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

    private ContactList() {
        addAll(Database.getInstance().getContacts());
        remove(Contact.getMyContact());
    }

    public void addContact(Contact contact) {
        Platform.runLater(() -> {
            boolean contains = contacts.stream().anyMatch(c -> c.getId().equals(contact.getId()));
            if (contains) {
                replaceContact(contact);
            } else {
                removeContact(contact.getId());
                contacts.add(contact);
            }
            ClassConnector.getInstance().updateContactListOnNetworkMap();
        });


    }

    private void replaceContact(Contact contact) {
        Contact old = new Contact();
        for (Contact c : contacts) {
            if (c.getId().equals(contact.getId())) {
                old = c;
            }
        }
        old.setId(contact.getId());
        old.setFirstname(contact.getFirstname());
        old.setLastname(contact.getLastname());
        old.setUrl(contact.getUrl());
        old.setLastOnline(contact.getLastOnline());
        old.setImage(contact.getImage());
        old.setNotifications(contact.getNotifications());
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

        return Contact.getMyContact();
    }
}
