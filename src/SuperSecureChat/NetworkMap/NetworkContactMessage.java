package SuperSecureChat.NetworkMap;

import SuperSecureChat.Contacts.Contact;
import javafx.scene.canvas.GraphicsContext;

public class NetworkContactMessage extends NetworkMessage {
    private Contact contact;

    public NetworkContactMessage(Contact contact, NetworkContact sender, NetworkContact reveiver) {
        super(sender, reveiver);
        this.contact = contact;
    }

    @Override
    public void draw(GraphicsContext gc) {
        super.draw(gc, contact.getJavaFXImage());
    }
}
