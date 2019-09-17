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
    private String text;
    private ArrayList<NetworkMessage> responses = new ArrayList<>();

    private boolean delete = false;

    public NetworkMessage(String text, NetworkContact sender, NetworkContact reveiver) {
        this.sender = sender;
        this.reveiver = reveiver;
        animation = 0;
        x = sender.getX();
        y = sender.getY();
        if (text.length() > 16) {
            text = text.substring(0, 16);
        }
        this.text = text;
    }

    public void draw(GraphicsContext gc, Image image, int size) {
        animation++;
        if (animation > ANIMATION_DURATION) {
            for (NetworkMessage response : responses) {
                ClassConnector.getInstance().sendNetworkMessageToNetworkMap(response, true);
            }
            this.delete = true;
            return;
        }
        calculateNewPosition();
        gc.drawImage(image, x, y, size, size);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(text, x + ((float) size / 2), y + 12 + size);

    }

    public void draw(GraphicsContext gc, Image image) {
        draw(gc, image, 24);

    }

    public void draw(GraphicsContext gc) {
        draw(gc, new Image(getClass().getResourceAsStream("/icons/round_chat_white_24dp.png")));
    }

    public void calculateNewPosition() {
        //double t = (-(((float) animation) / ANIMATION_DURATION) - 0.5) * 4;
        //double percent = 1 / (1 + Math.pow(Math.E, t));
        double percent = (float) animation / ANIMATION_DURATION;
        x = sender.getX() - (sender.getX() - reveiver.getX()) * percent;
        y = sender.getY() - (sender.getY() - reveiver.getY()) * percent * percent;


    }

    public void addResponse(NetworkMessage response) {
        responses.add(response);
    }

    public boolean getDelete() {
        return delete;
    }


}
