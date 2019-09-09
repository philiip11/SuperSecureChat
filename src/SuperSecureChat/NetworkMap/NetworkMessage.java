package SuperSecureChat.NetworkMap;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class NetworkMessage {
    private final int ANIMATION_DURATION = 60;

    private NetworkContact sender;
    private NetworkContact reveiver;
    private int animation;
    private double x;
    private double y;

    private boolean delete = false;

    public NetworkMessage(NetworkContact sender, NetworkContact reveiver) {
        this.sender = sender;
        this.reveiver = reveiver;
        animation = 0;
        x = sender.getX();
        y = sender.getY();
    }

    public void draw(GraphicsContext gc, Image image) {
        animation++;
        if (animation > ANIMATION_DURATION) {
            this.delete = true;
            return;
        }
        calculateNewPosition();
        gc.drawImage(image, x, y, 24, 24);

    }

    public void draw(GraphicsContext gc) {
        draw(gc, new Image(getClass().getResourceAsStream("/icons/round_chat_white_24dp.png")));
    }

    public void calculateNewPosition() {
        //double t = (-(((float) animation) / ANIMATION_DURATION) - 0.5) * 4;
        //double percent = 1 / (1 + Math.pow(Math.E, t));
        double percent = (float) animation / ANIMATION_DURATION;
        x = sender.getX() - (sender.getX() - reveiver.getX()) * percent;
        y = sender.getY() - (sender.getY() - reveiver.getY()) * percent;


    }

    public boolean getDelete() {
        return delete;
    }


}
