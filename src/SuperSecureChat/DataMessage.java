package SuperSecureChat;

import SuperSecureChat.Contacts.Contact;

public class DataMessage extends Message {
    public DataMessage(String id, String referencId, Contact sender, Contact receiver, String text, String data, String trace, int created, int read, int received) {
        super(id, referencId, sender, receiver, text, data, trace, created, read, received);
    }
}
