package SuperSecureChat.NetworkMap;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class NetworkIconMessage extends NetworkMessage {
    private Image image;

    public NetworkIconMessage(Image image, NetworkContact sender, NetworkContact reveiver) {
        super("", sender, reveiver);
        this.image = image;
    }

    @Override
    public void draw(GraphicsContext gc) {
        super.draw(gc, image, 48);
    }
}
