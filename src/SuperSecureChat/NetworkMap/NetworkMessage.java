package SuperSecureChat.NetworkMap;

import SuperSecureChat.ClassConnector;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;

public class NetworkMessage {
    private final int ANIMATION_DURATION = 60;

    private NetworkContact sender;
    private NetworkContact reveiver;
    private int animation;
    private double x;
    private double y;
    private double ax;
    private double ay;
    private String text;
    private ArrayList<NetworkMessage> responses = new ArrayList<>();

    private boolean delete = false;

    public NetworkMessage(String text, NetworkContact sender, NetworkContact reveiver) {
        this.sender = sender;
        this.reveiver = reveiver;
        animation = 0;
        x = sender.getX();
        y = sender.getY();
        ax = 0;
        ay = 0;
        if (text.length() > 16) {
            text = text.substring(0, 16);
        }
        this.text = text;
    }

    public void draw(GraphicsContext gc, Image image, int size) {
        calculateNewPosition();
        if (reachedTarget()) {
            for (NetworkMessage response : responses) {
                ClassConnector.getInstance().sendNetworkMessageToNetworkMap(response, true);
            }
            this.delete = true;
            return;
        }
        gc.drawImage(image, x, y, size, size);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(text, x + ((float) size / 2), y + 12 + size);

    }

    private boolean reachedTarget() {
        return Math.abs(ax) < 1 && Math.abs(ay) < 1;
    }

    public void draw(GraphicsContext gc, Image image) {
        draw(gc, image, 24);

    }

    public void draw(GraphicsContext gc) {
        draw(gc, new Image(getClass().getResourceAsStream("/icons/round_chat_white_24dp.png")));
    }

    public void calculateNewPosition() {
        if (reveiver.getX() != x) {
            ax += (reveiver.getX() - x) / 100;
            x = x + ax;
            ax = ax * 0.85;
        }
        if (reveiver.getY() != y) {
            ay += (reveiver.getY() - y) / 100;
            y = y + ay;
            ay = ay * 0.85;
        }
    }

    public void addResponse(NetworkMessage response) {
        responses.add(response);
    }

    public boolean getDelete() {
        return delete;
    }


}
