package org.kittykat.cat65.core.extraChips;

import org.kittykat.cat65.ui.window.Window;

public class LCD extends Window {
    public LCD() {
        super();
        // ToDo: load LCD sprite
        // ToDo: init CHR-RAM
    }

    @Override
    public void updateWindow() {
        // ToDo: update visuals
    }

    /**
     * bits 0-3: Data
    **/
    public int get(int pinState) {
        return 0x00;
    }
    /**
     * bits 0-3: Data<br>
     * bit   4:  RS<br>
     * bit   5:  R/W<br>
     * bit   6:  E
    **/
    public void set(int pinState) {

    }
}
