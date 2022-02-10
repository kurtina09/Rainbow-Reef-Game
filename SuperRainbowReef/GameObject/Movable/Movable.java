package SuperRainbowReef.GameObject.Movable;

import SuperRainbowReef.GameObject.GameObject;
import java.awt.image.BufferedImage;

/**
 *
 * @author motiveg
 */
public class Movable extends GameObject {

    protected double speed;

    public Movable() {
    }

    public Movable(BufferedImage img, int x, int y, double speed) {
        super(x, y, img, null);
        this.speed = speed;
    }

    public double getSpeed() {
        return this.speed;
    }

}
