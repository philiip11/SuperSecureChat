package SuperSecureChat;

import SuperSecureChat.Contacts.Contact;
import org.json.simple.JSONObject;

public class Message {

    private String id;                  // Eindeutige ID der Nachricht, bestehend aus Nutzername des Senders und seinem lokalen Counter
    private String referenceId;          // Referenz auf andere Nachricht, bspw. für Antwort oder Korrektur
    private Contact sender;             // Absender
    private Contact reciever;           // Empfänger
    private String text;                // Nachrichttext
    private String data;                // ggf. Datei
    private String trace;               // Spur, über welche PCs ist die Nachricht geschickt (Nutzername des PCs, Uhrzeit, IP?)
    private long created;               // Erstelldatum
    private int read;                   // Nachricht gelesen?  1 = ja, 0 = nein
    private int received;               // Nachricht beim Empfänger angekommen?

    public Message(String id, String referenceId, Contact sender, Contact reciever, String text, String base64data, String trace, long created, int read, int received) {
        this.id = id;
        this.referenceId = referenceId;
        this.sender = sender;
        this.reciever = reciever;
        this.text = text;
        this.data = base64data;
        this.trace = trace;
        this.created = created;
        this.read = read;
        this.received = received;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", referenceId='" + referenceId + '\'' +
                ", sender=" + sender +
                ", reciever=" + reciever +
                ", text='" + text + '\'' +
                ", data=" + data +
                ", trace='" + trace + '\'' +
                ", created=" + created +
                ", read=" + read +
                ", received=" + received +
                '}';
    }

    public String toJSONString() {
        return toJSON().toString();

    }

    @SuppressWarnings({"unchecked", "WeakerAccess"})
    public JSONObject toJSON() {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("id", id);
        jsonMessage.put("referenceID", referenceId);
        jsonMessage.put("sender", sender.toJSON());
        jsonMessage.put("reciever", reciever.toJSON());
        jsonMessage.put("text", text);
        jsonMessage.put("data", data);
        jsonMessage.put("trace", trace);
        jsonMessage.put("created", created);
        jsonMessage.put("read", read);
        jsonMessage.put("received", received);
        return jsonMessage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReferencId() {
        return referenceId;
    }

    public void setReferencId(String referencId) {
        this.referenceId = referencId;
    }

    public Contact getSender() {
        return sender;
    }

    public void setSender(Contact sender) {
        this.sender = sender;
    }

    public Contact getReciever() {
        return reciever;
    }

    public void setReciever(Contact reciever) {
        this.reciever = reciever;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTrace() {
        return trace;
    }

    public void setTrace(String trace) {
        this.trace = trace;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public int getReceived() {
        return received;
    }

    public void setReceived(int received) {
        this.received = received;
    }
}
