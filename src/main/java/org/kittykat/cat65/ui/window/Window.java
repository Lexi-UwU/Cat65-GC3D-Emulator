package org.kittykat.cat65.ui.window;

import javafx.scene.layout.VBox;
import org.kittykat.cat65.Cat65;

public abstract class Window extends VBox {
    public Window() {
        super(Cat65.SPACING);
        getStyleClass().add("window");
    }

    public void updateWindow() {}
}
