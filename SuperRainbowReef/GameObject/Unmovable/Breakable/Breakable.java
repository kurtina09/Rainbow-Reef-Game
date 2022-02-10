package SuperRainbowReef.GameObject.Unmovable.Breakable;

import SuperRainbowReef.GameObject.Unmovable.Unmovable;
import java.awt.image.BufferedImage;

/**
 *
 * @author monalimirel
 */
public class Breakable extends Unmovable {

    public int pointValue;

    public Breakable(int x, int y, int width, int height, BufferedImage img, int pointValue) {
        super(x, y, width, height, img);
        this.pointValue = pointValue;
    }

    public int getPointValue() {
        return this.pointValue;
    }
}
