package SuperSecureChat.Contacts;

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
        contacts.add(contact);
    }

    private void removeContact(String id) {
        for (Contact c : contacts) {
            if (c.getId().equals(id)) {
                contacts.remove(c);
            }
        }
    }

    public ObservableList<Contact> getAllContacts() {
        return contacts;
    }


}
