package org.kittykat.cat65.ui.window.videoCard;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.kittykat.cat65.Cat65;
import org.kittykat.cat65.ui.window.Window;

public class VideoSettingsWindow extends Window {
    public boolean vSync = true;

    public VideoSettingsWindow() {
        super();

        Label lbl_vSync = new Label("V-Sync:");
        CheckBox check_vSync = new CheckBox();
        check_vSync.setSelected(true);
        check_vSync.setOnAction(event -> vSync = check_vSync.isSelected());

        HBox vSyncBox = new HBox(Cat65.SPACING);
        vSyncBox.getChildren().addAll(lbl_vSync, check_vSync);

        getChildren().add(vSyncBox);
    }
}
