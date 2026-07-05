package org.kittykat.cat65.ui.window;

import javafx.scene.control.Label;

public abstract class WindowWithTitle extends Window {
    protected final Label lbl_winTitle;

    public WindowWithTitle(String title) {
        super();
        lbl_winTitle = new Label(title);
        lbl_winTitle.getStyleClass().add("win-title");
        getChildren().add(lbl_winTitle);
    }
}
