package SuperSecureChat.Chat;

import SuperSecureChat.Contacts.Contact;
import SuperSecureChat.Message;

import java.time.Instant;

public class ReferenceMessage extends Message {
    public ReferenceMessage() {

    }

    public ReferenceMessage(Message message, String s) {
        setId(Contact.getMyContact().getId() + Instant.now().getEpochSecond() + "r" + (Math.random() * 100000));
        setSender(Contact.getMyContact());
        setReceiver(message.getSender());
        setData(s);
        setCreated(Instant.now().getEpochSecond());
        setReferencId(message.getId());
        setText("");
    }
}
