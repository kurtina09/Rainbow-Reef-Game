package SuperRainbowReef.GameObject.Unmovable.Breakable;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author monalimirel
 */
public class PowerUp extends Breakable implements Observer {

    Rectangle blockRect;
    //private int height, width;
    private String type;

    public PowerUp(int x, int y, int width, int height, BufferedImage img, int pointValue, String type) {
        super(x, y, width, height, img, pointValue);
        this.height = img.getHeight();
        this.width = img.getWidth();
        blockRect = new Rectangle(x, y, width, height);
        this.type = type;
    }

    // GETTERS //
    public String getType() {
        return this.type;
    }

    public void draw(Graphics2D g) {
        g.drawImage(this.img, this.x, this.y, this);
    }

    @Override
    public void update(Observable o, Object arg) {
    }

}
