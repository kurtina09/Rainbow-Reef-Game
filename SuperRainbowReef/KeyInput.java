package SuperRainbowReef;

import SuperRainbowReef.GameObject.Movable.Katch;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Observable;

/**
 *
 * @author motiveg, monalimirel
 */
public class KeyInput extends Observable implements KeyListener {

    private final Katch katch;
    private final int leftKey;
    private final int rightKey;
    private final int aimLeftKey;
    private final int aimRightKey;
    private final int shootKey;

    public KeyInput(Katch katch) {
        this.katch = katch;
        this.leftKey = katch.getLeftKey();
        this.rightKey = katch.getRightKey();
        this.aimLeftKey = katch.getAimLeftKey();
        this.aimRightKey = katch.getAimRightKey();
        this.shootKey = katch.getShootKey();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == this.leftKey) {
            this.katch.switchLeftOn();
        }
        if (key == this.rightKey) {
            this.katch.switchRightOn();
        }
        if (key == this.shootKey) {
            this.katch.switchShootOn();
        }
        if (key == this.aimLeftKey) {
            this.katch.switchAimLeftOn();
        }
        if (key == this.aimRightKey) {
            this.katch.switchAimRightOn();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == this.leftKey) {
            this.katch.switchLeftOff();
        }
        if (key == this.rightKey) {
            this.katch.switchRightOff();
        }
        if (key == this.shootKey) {
            this.katch.switchShootOff();
        }
        if (key == this.aimLeftKey) {
            this.katch.switchAimLeftOff();
        }
        if (key == this.aimRightKey) {
            this.katch.switchAimRightOff();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }
}
