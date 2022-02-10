package SuperRainbowReef.GameObject.Unmovable.Unbreakable;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author monalimirel
 */
public class Wall extends Unbreakable implements Observer {

    Rectangle wallRect;
    //private int height, width;

    public Wall(int x, int y, int width, int height, BufferedImage img) {
        super(x, y, width, height, img);
        this.height = img.getHeight();
        this.width = img.getWidth();
        wallRect = new Rectangle(x, y, width, height);
    }

    public void draw(Graphics2D g) {
        g.drawImage(this.img, this.x, this.y, this);
    }

    @Override
    public void update(Observable o, Object arg) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
