package org.kittykat.cat65.ui.window.videoCard;

import javafx.scene.image.ImageView;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.WritableImage;
import org.kittykat.cat65.ui.window.Window;

import java.nio.IntBuffer;

public class ScreenWindow extends Window {
    public ScreenWindow(PixelBuffer<IntBuffer> pixelBuffer) {
        super();

        WritableImage image = new WritableImage(pixelBuffer);
        ImageView screen = new ImageView(image);

        screen.getStyleClass().add("screen");
        screen.setSmooth(false);

        getChildren().add(screen);
    }
}
