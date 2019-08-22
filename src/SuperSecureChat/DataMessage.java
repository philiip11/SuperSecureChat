package SuperSecureChat;

import SuperSecureChat.Contacts.Contact;

public class DataMessage extends Message {
    public DataMessage(String id, String referencId, Contact sender, Contact reciever, String text, String data, String trace, int created, int read, int received) {
        super(id, referencId, sender, reciever, text, data, trace, created, read, received);
    }
}
