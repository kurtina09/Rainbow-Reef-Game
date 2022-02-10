package SuperRainbowReef;

import java.util.Observable;

/**
 *
 * @author monalimirel, motiveg
 */
public class GameObservable extends Observable {

    @Override
    protected synchronized void setChanged() {
        super.setChanged();
    }

}
