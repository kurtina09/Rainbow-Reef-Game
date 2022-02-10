package SuperRainbowReef.GameObject.Unmovable;

import SuperRainbowReef.GameObject.GameObject;
import java.awt.image.BufferedImage;

/**
 *
 * @author monalimirel, motiveg
 */
public abstract class Unmovable extends GameObject {

    public Unmovable(int x, int y, int width, int height, BufferedImage img) {
        super(x, y, width, height, img);
    }
}
