package SuperSecureChat.Contacts;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ContactList {

    private static final ContactList INSTANCE = new ContactList();
    private ObservableList<Contact> contacts = FXCollections.observableArrayList();

    public static ContactList getInstance() {
        return INSTANCE;
    }

    public void addContact(Contact contact) {
        removeContact(contact.getId());
        Platform.runLater(() -> contacts.add(contact));

    }

    private void removeContact(String id) {
        contacts.removeIf(c -> c.getId().equals(id));
    }

    public ObservableList<Contact> getAllContacts() {
        return contacts;
    }


}
