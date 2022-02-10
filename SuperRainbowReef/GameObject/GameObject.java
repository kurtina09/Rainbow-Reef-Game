package SuperRainbowReef.GameObject;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import javax.swing.JComponent;

/**
 *
 * @author monalimirel, motiveg
 */
public class GameObject extends JComponent {

    protected Rectangle objectRectangle;
    protected int x, y, width, height;
    protected BufferedImage img;

    // CONSTRUCTORS //
    public GameObject() {
    }

    public GameObject(BufferedImage img, ImageObserver observer) {
        this.img = img;
        this.x = 0; // set after construction
        this.y = 0; // set after construction
        try {
            this.width = img.getWidth(observer);
            this.height = img.getHeight(observer);
        } catch (NullPointerException e) {
            System.out.println("NullPointerException for GameObject(Image, ImageObserver)\n");
            this.width = 0;
            this.height = 0;
        }
        this.objectRectangle.setBounds(this.x, this.y, this.width, this.height);
    }

    public GameObject(int x, int y, BufferedImage img, ImageObserver observer) {
        this.img = img;
        this.x = x;
        this.y = y;
        try {
            this.width = img.getWidth(observer);
            this.height = img.getHeight(observer);
        } catch (NullPointerException e) {
            System.out.println("NullPointerException for GameObject(int, int, Image, ImageObserver)\n");
            this.width = 0;
            this.height = 0;
        }
        this.objectRectangle = new Rectangle(x, y, this.width, this.height);
    }

    public GameObject(int x, int y, int width, int height, BufferedImage img) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.img = img;
        this.objectRectangle = new Rectangle(x, y, this.width, this.height);
    }

    // SETTERS //
    public void setX(int newX) {
        this.x = newX;
        this.objectRectangle.setLocation(newX, this.y);
    }

    public void setY(int newY) {
        this.y = newY;
        this.objectRectangle.setLocation(this.x, newY);
    }

    @Override
    public void setLocation(int newX, int newY) {
        this.x = newX;
        this.y = newY;
        this.objectRectangle = new Rectangle(newX, newY);
    }

    @Override
    public void setLocation(Point newLocation) {
        this.x = newLocation.x;
        this.y = newLocation.y;
        this.objectRectangle.setLocation(newLocation);
    }

    public void setWidth(int newWidth) {
        this.width = newWidth;
        this.objectRectangle.setSize(newWidth, this.height);
    }

    public void setHeight(int newHeight) {
        this.height = newHeight;
        this.objectRectangle.setSize(this.width, newHeight);
    }

    @Override
    public void setSize(int newWidth, int newHeight) {
        this.width = newWidth;
        this.height = newHeight;
        this.objectRectangle.setSize(newWidth, newHeight);
    }

    @Override
    public void setSize(Dimension dim) {
        this.objectRectangle.setSize(dim);
    }

    public void setObjectRectangle(int x, int y, int width, int height) {
        this.objectRectangle = new Rectangle(x, y, width, height);
    }

    public void setImage(BufferedImage img) {
        this.img = img;
    }

    public void setImage(BufferedImage img, ImageObserver observer) {
        this.img = img;
        try {
            this.height = img.getWidth(observer);
            this.width = img.getHeight(observer);
        } catch (NullPointerException e) {
            this.height = 0;
            this.width = 0;
        }
        this.objectRectangle.setSize(this.width, this.height);
    }

    // GETTERS //
    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public Point getLocation() {
        return new Point(this.x, this.y);
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public Dimension getSize() {
        return this.objectRectangle.getSize();
    }

    public Rectangle getObjectRectangle() {
        return new Rectangle(x, y, width, height);
    }

    public Image getImage() {
        return this.img;
    }

    // test
    @Override
    public String toString() {
        StringBuilder objectData = new StringBuilder();
        objectData.append("X: ");
        objectData.append(this.x);
        objectData.append("\tY: ");
        objectData.append(this.y);
        objectData.append("\tWidth: ");
        objectData.append(this.width);
        objectData.append("\tHeight: ");
        objectData.append(this.height);
        objectData.append("\n");
        return objectData.toString();
    }

}
