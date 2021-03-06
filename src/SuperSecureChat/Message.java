package SuperSecureChat;

import SuperSecureChat.Contacts.Contact;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@SuppressWarnings("unused")
public class Message {

    private String id;                  // Eindeutige ID der Nachricht, bestehend aus Nutzername des Senders und seinem lokalen Counter
    private String referenceId;          // Referenz auf andere Nachricht, bspw. für Antwort oder Korrektur
    private Contact sender;             // Absender
    private Contact receiver;           // Empfänger
    private String text;                // Nachrichttext
    private String data;                // ggf. Datei
    private String trace;               // Spur, über welche PCs ist die Nachricht geschickt (Nutzername des PCs, Uhrzeit, IP?)
    private long created;               // Erstelldatum
    private long read;                   // Nachricht gelesen?  1 = ja, 0 = nein
    private long received;               // Nachricht beim Empfänger angekommen?

    public Message(String id, String referenceId, Contact sender, Contact receiver, String text, String base64data, String trace, long created, long read, long received) {
        this.id = id;
        this.referenceId = referenceId;
        this.sender = sender;
        this.receiver = receiver;
        this.text = text;
        this.data = base64data;
        this.trace = trace;
        this.created = created;
        this.read = read;
        this.received = received;
    }

    public Message() {

    }

    @SuppressWarnings("unchecked")
    public static Message fromJSON(String json) {
        Message message = new Message();
        try {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(json);
            try {
                message.setId(jsonObject.get("id").toString());
                message.setReferencId(jsonObject.get("referenceID").toString());
                message.setSender(Contact.fromJSON(jsonObject.get("sender").toString()));
                message.setReceiver(Contact.fromJSON(jsonObject.get("receiver").toString()));
                jsonObject.putIfAbsent("text", "");
                jsonObject.putIfAbsent("data", "");
                message.setText(jsonObject.get("text").toString());
                message.setData(jsonObject.get("data").toString());
                message.setTrace(jsonObject.get("trace").toString());
                message.setCreated((long) jsonObject.get("created"));
                message.setRead((long) jsonObject.get("read"));
                message.setReceived((long) jsonObject.get("received"));
                message.setId(jsonObject.get("id").toString());

            } catch (NullPointerException e) {
                System.out.println("NullPointerException bei Nachricht " + jsonObject.get("id").toString());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return message;
    }

    public String toJSONString() {
        return toJSON().toString();

    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", referenceId='" + referenceId + '\'' +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", text='" + text + '\'' +
                ", data=" + data +
                ", trace='" + trace + '\'' +
                ", created=" + created +
                ", read=" + read +
                ", received=" + received +
                '}';
    }

    @SuppressWarnings({"unchecked", "WeakerAccess"})
    public JSONObject toJSON() {
        try {
            JSONObject jsonMessage = new JSONObject();
            jsonMessage.put("id", id);
            jsonMessage.put("referenceID", referenceId);
            jsonMessage.put("sender", sender.toJSON());
            jsonMessage.put("receiver", receiver.toJSON());
            jsonMessage.put("text", text);
            jsonMessage.put("data", data);
            jsonMessage.put("trace", trace);
            jsonMessage.put("created", created);
            jsonMessage.put("read", read);
            jsonMessage.put("received", received);
            return jsonMessage;
        } catch (NullPointerException e) {
            System.out.println("NullPointer bei Nachricht " + id);
            return null;
        }
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

    public Contact getReceiver() {
        return receiver;
    }

    public void setReceiver(Contact receiver) {
        this.receiver = receiver;
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

    public long getRead() {
        return read;
    }

    public void setRead(long read) {
        this.read = read;
    }

    public long getReceived() {
        return received;
    }

    public void setReceived(long received) {
        this.received = received;
    }
}
