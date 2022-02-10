package SuperRainbowReef.GameObject.Movable;

import SuperRainbowReef.GameObject.GameObject;
import SuperRainbowReef.GameObject.Unmovable.Breakable.BigLegs;
import SuperRainbowReef.GameObject.Unmovable.Breakable.CoralBlocks;
import SuperRainbowReef.GameObject.Unmovable.Breakable.PowerUp;
import SuperRainbowReef.GameObject.Unmovable.Unbreakable.SolidBlocks;
import SuperRainbowReef.GameObject.Unmovable.Unbreakable.Wall;
import SuperRainbowReef.GameWorld;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ConcurrentModificationException;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author motiveg, monalimirel
 */
/**
 * TODO/PLANNED/POSSIBILITIES:
 * - come up with a reasonable/fair speed up scheme (in progress)
 * - use double for velocities?
 * - life modification when power up is destroyed
 * - life modification when out of bounds
 * - split when power up is destroyed
 */
public class Pop extends Movable implements Observer {

    private int score = 0;
    private int life = 3;
    private int mapSizeX, mapSizeY, spawnPointX, spawnPointY, numberKatchCollisions;
    private double xVelocity, yVelocity, minSpeed, maxSpeed; // used for speed increase
    
    private double xD, yD;// test

    private boolean isDead, isMoving;

    private Katch katch;
    
    private GameWorld worldReference;

    public Pop(GameWorld worldReference, Katch katch, BufferedImage img, double speed) {
        super(img, katch.getX(), katch.getY(), speed);
        this.xVelocity = 0;
        this.yVelocity = 0;
        this.minSpeed = speed;
        this.maxSpeed = 1.5; // change max speed here
        this.katch = katch;
        this.worldReference = worldReference;
        this.isMoving = false;

        // initialize position above katch
        this.x += this.katch.getWidth() / 4;
        this.y -= this.height + 2;
        this.spawnPointX = x;
        this.spawnPointY = y;

        this.mapSizeX = worldReference.getMapWidth();
        this.mapSizeY = worldReference.getMapHeight();

        this.numberKatchCollisions = 0;
        
        // test
        this.xD = x;
        this.yD = y;
    }

    // SETTERS //
    public void setMoveOn() {
        this.isMoving = true;
    }

    public void setMoveOff() {
        this.isMoving = false;
    }

    public void setXVelocity(double xv) {
        this.xVelocity = xv;
    }

    public void setYVelocity(double yv) {
        this.yVelocity = yv;
    }

    public void updateScore(int blockPointValue) {
        score += blockPointValue;
    }

    public boolean isDead() {
        if (this.life <= 0) {
            return true;
        }
        return false;
    }
    
    // test
    public void setXD(double xD) {
        this.xD = xD;
    }
    public void setYD(double yD) {
        this.yD = yD;
    }

    // GETTERS //
    public int getScore() {
        return score;
    }

    public int getLife() {
        return life;
    }

    public void draw(ImageObserver iobs, Graphics2D g) {
        // TODO: try to draw Katch as an animation
        // ONE SOLUTION: store gif and an ImageIcon and draw like so...
        // g.drawImage(imageIcon.getImage(), someX, someY, this);
        g.drawImage(img, (int)Math.round(xD), (int)Math.round(yD), iobs);
    }

    @Override
    public void update(Observable o, Object arg) {
        updateMove();
        updateCollision();
    }

    private void updateMove() {
        // move based on current velocity
        if (this.isMoving && !isDead) {
            if (numberKatchCollisions % 500 == 0 && numberKatchCollisions != 0 && speed <= maxSpeed) {
                speed = speed + 0.5;
                if (yVelocity < 0) {
                    yVelocity--;
                } else {
                    yVelocity++;
                }
                System.out.println("Speed: " + yVelocity);

            }

            this.xD += xVelocity;
            this.yD += yVelocity;
            checkLimit();
        }
        // stay above katch while not shot
        if (!(this.isMoving)) {
            this.xD = this.katch.getX() + (this.katch.getWidth() / 4);
        }

    }

    private void checkLimit() {
        if (this.yD <= 0 || this.yD >= mapSizeY) {
            respawn();
        }
        if (this.xD <= 0 || this.xD >= mapSizeX) {
            respawn();
        }
    }

    public void respawn() {
        xVelocity = 0;
        yVelocity = 0;
        setMoveOff();
        life--;
        this.katch.respawn();
        this.yD = spawnPointY;
        this.xD = this.katch.getX() + (this.katch.getWidth() / 4);
        this.katch.switchCanShootOn();
    }

    private void updateCollision() {
        GameObject collisionObject = closestCollisionObject();
        if (collisionObject instanceof Katch) {
            katchCollision();
        } else {
            genericObjectCollision(collisionObject);
        }
    }

    /**
     * Credit for collision direction checking:
     * https://stackoverflow.com/questions/21652416/java-get-direction-of-rectangle-collision
     */
    private void genericObjectCollision(GameObject closest_object) {
        if (closest_object != null) {

            // set pop at collision point first
            this.setXD(xD + xVelocity);
            this.setYD(yD + yVelocity);

            // pop's directional locations
            int pop_left = (int)Math.round(xD);
            int pop_right = (int)Math.round(xD) + width;
            int pop_top = (int)Math.round(yD);
            int pop_bottom = (int)Math.round(yD) + height;

            // game object's directional locations
            int object_left = closest_object.getX();
            int object_right = closest_object.getX() + closest_object.getWidth();
            int object_top = closest_object.getY();
            int object_bottom = closest_object.getY() + closest_object.getHeight();

            // delete object before continuing
            updateGameObject(closest_object);

            // create array of 4 ints to store directional differences
            int max_int = Integer.MAX_VALUE;
            int[] intersections = new int[]{max_int, max_int, max_int, max_int};

            // check for intersection differences
            if (pop_right > object_left && pop_left < object_left) { // 0: pop on left
                intersections[0] = pop_right - object_left;
            }
            if (pop_left < object_right && pop_right > object_right) { // 1: pop on right
                intersections[1] = object_right - pop_left;
            }
            if (pop_top < object_bottom && pop_bottom > object_bottom) { // 2: pop on bottom
                intersections[2] = object_bottom - pop_top;
            }
            if (pop_bottom > object_top && pop_top < object_top) { // 3: pop on top
                intersections[3] = pop_bottom - object_top;
            }

            // get the intersection with the least distance
            int min = max_int;
            int min_index = -1;
            for (int i = 0; i < 4; i++) {
                if (intersections[i] < min) {
                    min = intersections[i];
                    min_index = i;
                }
            }

            // debug purposes
            System.out.println("LEFT collision distance:" + intersections[0]);
            System.out.println("RIGHT collision distance:" + intersections[1]);
            System.out.println("BOTTOM collision distance:" + intersections[2]);
            System.out.println("TOP collision distance:" + intersections[3]);

            // debug purposes
            String direction = "none";
            if (min_index == 0) {
                direction = "left";
            }
            if (min_index == 1) {
                direction = "right";
            }
            if (min_index == 2) {
                direction = "bottom";
            }
            if (min_index == 3) {
                direction = "top";
            }

            // debug purposes
            System.out.println("Pop collided from the " + direction + "\n");

            // relfect based on collision direction
            if (min_index == 0) { // pop collided from the left
                xVelocity *= -1;
            }
            if (min_index == 1) { // pop collided from the right
                xVelocity *= -1;
            }
            if (min_index == 2) { // pop collided from the bottom
                yVelocity *= -1;
            }
            if (min_index == 3) { // pop collided from the top
                yVelocity *= -1;
            }
        }
    }

    private void katchCollision() {

        this.worldReference.playSound(1);
        this.worldReference.resetSoundEffect(1);

        // increment number of collisions for yVelocity speed up
        numberKatchCollisions++;
        System.out.println("Katch collisions: " + numberKatchCollisions);

        // set pop at collision point first
        this.setXD(xD + xVelocity);
        this.setYD(yD + yVelocity);

        // compare the center of pop to the landing spot on katch
        // katch section diagram: ____b1____b2____b3____b4____
        // s=section, b=bound      s1 || s2 || s3 || s4 || s5 
        
        // s1: reflect left if currently moving right
        //      increase left velocity if currently moving left
        //      increase left velocity by 1 if xVelocity == 0
        
        // s2: reflect left if currently moving right
        //      increase left velocity by 1 if xVelocity == 0
        //      maintain left velocity otherwise
        
        // s3: reflect with 0 xVelocity
        
        // s4: reflect right if currently moving left
        //      increase right velocity by 1 if xVelocity == 0
        //      maintain right velocity otherwise
        
        // s5: reflect right if currently moving left
        //      increase right velocity if currently moving right
        //      increase right velocity by 1 if xVelocity == 0
        
        int pop_x_center = (int)Math.round(xD) + (width / 2);

        int katch_x = this.katch.getX();

        int num_sections = 5;
        int num_bounds = num_sections - 1;
        int katch_sectional_width = this.katch.getWidth() / num_sections; // should be 80/5=16

        int[] katch_bounds = new int[num_bounds];
        for (int i = 0; i < num_bounds; i++) {
            katch_bounds[i] = katch_x + (katch_sectional_width * (i + 1));
        }

        // SECTION 1
        if (pop_x_center < katch_bounds[0]) {
            // reflect left if currently moving right
            if (xVelocity > 0) {
                xVelocity *= -1;
                // increase left velocity if currently moving left
            } else if (xVelocity == 0) {
                xVelocity = -1;
                // increase left velocity by 1 if xVelocity == 0
            } else {
                xVelocity -= 1;
            }
            
        } // SECTION 2
        else if (pop_x_center < katch_bounds[1]) {
            // reflect left if currently moving right
            if (xVelocity > 0) {
                xVelocity *= -1;
            } // increase left velocity by 1 if xVelocity == 0
            else if (xVelocity == 0) {
                xVelocity = -1;
            }
            // maintain left velocity otherwise

        } // SECTION 3
        else if (pop_x_center < katch_bounds[2]) {
            // reflect with 0 xVelocity
            xVelocity = 0;
            
        } // SECTION 4
        else if (pop_x_center < katch_bounds[3]) {
            // reflect right if currently moving left
            if (xVelocity < 0) {
                xVelocity *= -1;
            } // increase right velocity by 1 if xVelocity == 0
            else if (xVelocity == 0) {
                xVelocity = 1;
            }
            // maintain right velocity otherwise

        } // SECTION 5
        else {
            // reflect right if currently moving left
            if (xVelocity < 0) {
                xVelocity *= -1;
                // increase right velocity if currently moving right
            } else if (xVelocity == 0) {
                xVelocity = 1;
                // increase right velocity by 1 if xVelocity == 0
            } else {
                xVelocity += 1;
            }
        }
        // finally, reflect up
        yVelocity *= -1;
    }

    private GameObject closestCollisionObject() {
        Rectangle future_pop = new Rectangle(this.getObjectRectangle());
        future_pop.setLocation((int)Math.round(xD + xVelocity), (int)Math.round(yD + yVelocity));

        GameObject closest_object = null;

        // check wall collisions
        for (Wall curr : this.worldReference.getWalls()) {
            if (future_pop.intersects(curr.getObjectRectangle())) {
                closest_object = getClosestObject(closest_object, curr);
            }
        }
        // check solid block collisions
        for (SolidBlocks curr : this.worldReference.getSolidBlocks()) {
            if (future_pop.intersects(curr.getObjectRectangle())) {
                closest_object = getClosestObject(closest_object, curr);
            }
        }
        // check coral block collisions
        for (CoralBlocks curr : this.worldReference.getCoralBlocks()) {
            if (future_pop.intersects(curr.getObjectRectangle())) {
                closest_object = getClosestObject(closest_object, curr);
            }
        }
        // check big leg collisions
        for (BigLegs curr : this.worldReference.getBigLegs()) {
            if (future_pop.intersects(curr.getObjectRectangle())) {
                closest_object = getClosestObject(closest_object, curr);
            }
        }
        // check power up collisions
        for (PowerUp curr : this.worldReference.getPowerUp()) {
            if (future_pop.intersects(curr.getObjectRectangle())) {
                closest_object = getClosestObject(closest_object, curr);
            }
        }
        // check katch collisions
        if (!(this.katch.getShootStatus())) {
            if (future_pop.intersects(this.katch.getObjectRectangle())) {
                closest_object = getClosestObject(closest_object, this.katch);
            }
        }
        return closest_object; // null if no collisions
    }

    private GameObject getClosestObject(GameObject obj1, GameObject obj2) {
        if (obj1 != null) {
            Rectangle future_pop = new Rectangle(this.getObjectRectangle());
            future_pop.setLocation((int)Math.round(xD + xVelocity), (int)Math.round(yD + yVelocity));

            int xc = (int)Math.round(xD) + (width / 2);
            int yc = (int)Math.round(yD) + (height / 2);

            int xc_obj1 = obj1.getX() + (obj1.getWidth() / 2);
            int yc_obj1 = obj1.getY() + (obj1.getHeight() / 2);

            int xc_obj2 = obj2.getX() + (obj2.getWidth() / 2);
            int yc_obj2 = obj2.getY() + (obj2.getHeight() / 2);

            double distance1 = Math.sqrt(Math.pow((xc - xc_obj1), 2) + Math.pow((yc - yc_obj1), 2));
            double distance2 = Math.sqrt(Math.pow((xc - xc_obj2), 2) + Math.pow((yc - yc_obj2), 2));

            if (distance1 < distance2) {
                return obj1;
            } else {
                return obj2;
            }
        }
        return obj2; // if there's a null object, it will be obj1 by default
    }

    private void updateGameObject(GameObject gameobject) {
        try {
            int soundNum = -1;
            if (gameobject instanceof Wall) {
                soundNum = 2;
                this.worldReference.playSound(2);
            }
            if (gameobject instanceof SolidBlocks) {
                soundNum = 2;
                this.worldReference.playSound(2);
            }
            if (gameobject instanceof CoralBlocks) {
                soundNum = 3;
                this.worldReference.playSound(3);
                updateScore(((CoralBlocks) gameobject).getPointValue());
                this.worldReference.getCoralBlocks().remove((CoralBlocks) gameobject);
            }
            if (gameobject instanceof BigLegs) {
                soundNum = 4;
                this.worldReference.playSound(4);
                updateScore(((BigLegs) gameobject).getPointValue());
                this.worldReference.getBigLegs().remove((BigLegs) gameobject);
            }
            if (gameobject instanceof PowerUp) {
                soundNum = 3;
                this.worldReference.playSound(3);
                this.powerUpUpdate((PowerUp) gameobject);
                updateScore(((PowerUp) gameobject).getPointValue());
                this.worldReference.getPowerUp().remove((PowerUp) gameobject);
            }
            if (soundNum >= 0 && soundNum <= 5) {
                this.worldReference.resetSoundEffect(soundNum);
            }
        } catch (ConcurrentModificationException e) {
            // indicate that concurrent modification was attempted
        }
    }

    private void powerUpUpdate(PowerUp powerup) {
        if (powerup.getType().equals("life")) {
            life++;
        }
        if (powerup.getType().equals("split")) {
            popSplit();
        }
    }

    private void popSplit() {
        // RULES:
        // - change the current pop and create a new pop
        // - both pops will be set side by side under the collided powerup block
        // - each pop will then continue moving with their new velocities
        // - only when both pops die will it count as a lost life and need to respawn
        // - the original katch will return to his original size

    }

}
