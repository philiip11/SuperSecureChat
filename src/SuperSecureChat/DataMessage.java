package SuperSecureChat;

import SuperSecureChat.Contacts.Contact;

import java.util.Date;

public class DataMessage extends Message {
    public DataMessage(String id, String referencId, Contact sender, Contact reciever, String text, Byte[] data, String trace, Date created, int read, int received) {
        super(id, referencId, sender, reciever, text, data, trace, created, read, received);
    }
}
