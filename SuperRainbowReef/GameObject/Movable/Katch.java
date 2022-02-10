package SuperRainbowReef.GameObject.Movable;

import SuperRainbowReef.GameObject.Unmovable.Unbreakable.Wall;
import SuperRainbowReef.GameWorld;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;
//import javax.swing.ImageIcon;

/**
 *
 * @author motiveg, monalimirel
 */
/**
 * TODO/PLANNED/POSSIBILITIES: - be able to move up slightly to compensate for
 * limited angles - movement with mouse
 */
public class Katch extends Movable implements Observer {

    private int spawnPointX, spawnPointY;
    private int leftKey, rightKey, shootKey, aimLeftKey, aimRightKey;
    private int shootAngle;
    private final int minShootAngle = 210, maxShootAngle = 330;

    private boolean moveLeft, moveRight, shoot, canShoot, aimLeft, aimRight;

    private Pop pop;

    private GameWorld worldReference;

    //private ImageIcon imgicon;
    public Katch() {
    }

    public Katch(GameWorld worldReference, BufferedImage img, int x, int y, int speed, int leftKey, int rightKey, int shootKey, int aimLeftKey, int aimRightKey) {
        // TODO/FIX: use Area class if we want more precise collisions
        //           use intersect(Area rhs) to get the resulting collision Area
        //           use isEmpty() right after to see whether an intersection occurred
        super(img, x, y, speed);

        this.leftKey = leftKey;
        this.rightKey = rightKey;
        this.shootKey = shootKey;
        this.aimLeftKey = aimLeftKey;
        this.aimRightKey = aimRightKey;

        this.moveLeft = false;
        this.moveRight = false;
        this.shoot = false;
        this.canShoot = true;
        this.aimLeft = false;
        this.aimRight = false;

        this.spawnPointX = x;
        this.spawnPointY = y;

        this.worldReference = worldReference;

        this.shootAngle = 255; // 270 degrees faces up
    }

    //SETTERS
    public void switchLeftOn() {
        this.moveLeft = true;
    }

    public void switchRightOn() {
        this.moveRight = true;
    }

    public void switchShootOn() {
        this.shoot = true;
    }

    public void switchCanShootOn() {
        this.canShoot = true;
    }

    public void switchAimLeftOn() {
        this.aimLeft = true;
    }

    public void switchAimRightOn() {
        this.aimRight = true;
    }

    public void switchLeftOff() {
        this.moveLeft = false;
    }

    public void switchRightOff() {
        this.moveRight = false;
    }

    public void switchShootOff() {
        this.shoot = false;
    }

    public void switchCanShootOff() {
        this.canShoot = false;
    }

    public void switchAimLeftOff() {
        this.aimLeft = false;
    }

    public void switchAimRightOff() {
        this.aimRight = false;
    }

    public void setPop(Pop pop) {
        this.pop = pop;
    }

    /*
    public void setImageIcon(ImageIcon img) {
        this.imgicon = img;
    }
     */

    //GETTERS
    public int getLeftKey() {
        return this.leftKey;
    }

    public int getRightKey() {
        return this.rightKey;
    }

    public int getShootKey() {
        return this.shootKey;
    }

    public int getAimLeftKey() {
        return this.aimLeftKey;
    }

    public int getAimRightKey() {
        return this.aimRightKey;
    }

    public int getShootAngle() {
        return this.shootAngle;
    }

    public boolean getShootStatus() {
        return this.canShoot;
    }

    public void respawn() {
        this.x = spawnPointX;
        this.y = spawnPointY;
    }

    public void draw(Graphics2D g) {
        // TODO: try to draw Katch as an animation
        // ONE SOLUTION: store gif and an ImageIcon and draw like so...
        // g.drawImage(imageIcon.getImage(), someX, someY, this);
        drawAim(g);
        g.drawImage(img, x, y, this);
        //g.drawImage(imgicon.getImage(), x, y, this);
        //imgicon.paintIcon(this, g, x, y);
    }

    private void drawAim(Graphics2D g) {

        int x_origin = x + (width / 2);
        int line_length = 30;
        int half_triangle_width = 10;

        int[] xPoints = {x_origin + line_length + half_triangle_width, x_origin + line_length, x_origin + line_length};
        int[] yPoints = {y - 17, y - half_triangle_width - 17, y + half_triangle_width - 17};

        Line2D line = new Line2D.Double(x_origin, y - (this.pop.getHeight() / 2),
                x_origin + line_length, y - (this.pop.getHeight() / 2));
        Polygon triangle = new Polygon(xPoints, yPoints, 3);
        AffineTransform arrow_rotation = AffineTransform.getRotateInstance(Math.toRadians(shootAngle), line.getX1(), line.getY1());

        if (!shoot && canShoot) {
            g.setColor(Color.YELLOW);
            g.draw(arrow_rotation.createTransformedShape(line));
            g.draw(arrow_rotation.createTransformedShape(triangle));
        }
    }

    @Override
    public void update(Observable obj, Object arg) {
        shoot();
        updateAim();
        updateMove();
        updateWallCollision();
    }

    private void shoot() {
        if (shoot && canShoot) {

            // set pop's initial movement
            this.pop.setXVelocity(this.pop.getSpeed() * Math.cos(Math.toRadians(shootAngle)));
            this.pop.setYVelocity(this.pop.getSpeed() * Math.sin(Math.toRadians(shootAngle)));
            this.pop.setMoveOn();

            // disable aiming and shooting after pop moves
            switchCanShootOff();
            switchAimLeftOff();
            switchAimRightOff();
            this.shootAngle = 255;
        }
    }

    private void updateAim() {
        if (canShoot) {
            if (aimLeft) {
                shootAngle -= 15;
                if (shootAngle == 255) {
                    shootAngle = 240;
                }
                if (shootAngle == 285) {
                    shootAngle = 270;
                }
                if (shootAngle == 270) {
                    shootAngle = 255;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }
            if (aimRight) {
                shootAngle += 15;
                if (shootAngle == 285) {
                    shootAngle = 300;
                }
                if (shootAngle == 255) {
                    shootAngle = 270;
                }
                if (shootAngle == 270) {
                    shootAngle = 285;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }
            if (shootAngle < minShootAngle) {
                shootAngle = minShootAngle;
            }
            if (shootAngle > maxShootAngle) {
                shootAngle = maxShootAngle;
            }
        }
    }

    private void updateMove() {
        if (moveLeft) {
            x -= speed * 2;
        }
        if (moveRight) {
            x += speed * 2;
        }
    }

    private void updateWallCollision() {
        wallCollisionSide(getWall());
    }

    private Wall getWall() {
        Rectangle katch_rectangle = new Rectangle(x, y, width, height);
        for (Wall curr : this.worldReference.getWalls()) {
            if (katch_rectangle.intersects(curr.getObjectRectangle())) {
                return curr;
            }
        }
        return null;
    }

    private void wallCollisionSide(Wall wall) {
        if (wall != null) {
            // katch's directional locations
            int katch_left = x;
            int katch_right = x + width;

            // game object's directional locations
            Rectangle wallRect = new Rectangle(wall.getX(), wall.getY(), wall.getWidth(), wall.getHeight());
            int wall_left = wallRect.x;
            int wall_right = wallRect.x + wallRect.width;

            int max_int = Integer.MAX_VALUE;
            int[] intersections = new int[]{max_int, max_int};

            // check for intersection differences
            if (katch_right > wall_left && katch_left < wall_left) { // 0: katch on left
                intersections[0] = katch_right - wall_left;
            }
            if (katch_left < wall_right && katch_right > wall_right) { // 1: katch on right
                intersections[1] = wall_right - katch_left;
            }

            // get the intersection with the least distance
            int min = max_int;
            int min_index = -1;
            for (int i = 0; i < 2; i++) {
                if (intersections[i] < min) {
                    min = intersections[i];
                    min_index = i;
                }
            }

            // correct position based on collision direction
            if (min_index == 0) { // katch collided from the left
                x -= speed * 2;
            }
            if (min_index == 1) { // katch collided from the right
                x += speed * 2;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder katchData = new StringBuilder();
        katchData.append("X: ");
        katchData.append(this.x);
        katchData.append("\tY: ");
        katchData.append(this.y);
        katchData.append("\n");
        return katchData.toString();
        //System.out.printf("X: %d\tY: %d\n", this.x, this.y);
    }
}
