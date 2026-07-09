package org.kittykat.cat65.core.expansionDevices;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.layout.VBox;
import org.kittykat.cat65.Cat65;
import org.kittykat.cat65.EmuHelper;
import org.kittykat.cat65.core.CMU;
import org.kittykat.cat65.core.ExpansionData;
import org.kittykat.cat65.ui.window.videoCard.ScreenWindow;
import org.kittykat.cat65.ui.window.videoCard.VideoSettingsWindow;

import java.nio.IntBuffer;
import java.util.Arrays;

import static org.kittykat.cat65.EmuHelper.boolBit;

public class GraphicsCard extends ExpansionDevice{

    private final VideoSettingsWindow settings = new VideoSettingsWindow();

    private static final float CLOCK_MHZ = 25.175f;

    private static final int H_VISIBLE = 640;

    private static final int V_VISIBLE = 480;

    private static final int MAX_RAY_DISTANCE = 255;

    private final int[] frame = new int[H_VISIBLE * V_VISIBLE];
    private final int[] colorBuffer = new int[frame.length];
    private final PixelBuffer<IntBuffer> pixelBuffer =
            new PixelBuffer<>(H_VISIBLE, V_VISIBLE, IntBuffer.wrap(colorBuffer), PixelFormat.getIntArgbPreInstance());


    private volatile double timeAccumulator = 0f;

    public GraphicsCard(int portNum) {
        super(0b1111_1111_1111, portNum);
        Arrays.fill(frame, 0xff_000000);
        makeWindow();
        showWindow();
    }

    @Override
    @SuppressWarnings("NonAtomicOperationOnVolatileField")
    public void clock() {
        timeAccumulator += CLOCK_MHZ;
        while (timeAccumulator >= 1f) {
            //videoClock();
            timeAccumulator -= 1f;
        }
    }

    @Override
    protected int get(int relAddress, boolean cpu) {
        System.out.printf("%03x\n", relAddress);

        return 0xff;
    }

    @Override
    protected void set(int relAddress, int value, boolean cpu) {
        System.out.printf("%03x %02x\n", relAddress, value);
    }

    @Override
    public void updateWindow() {
        if ((!settings.vSync) || true) {
            //System.arraycopy(frame, 0, colorBuffer, 0, frame.length);
            //fillColor(255,0,0);
            renderRays();
            updateScreen();

            //System.out.println("bleh");
            //fillColor(255,0,0);
        }

    }

    public double invSqr(double x){
        return 1/Math.sqrt(x);
    }

    public int runRay(int x, int y, int z, int dirX, int dirY, int dirZ){
        //MAX_RAY_DISTANCE
        int TRAVELLED_DISTANCE = 0;

        double rayX = x;
        double rayY = y;
        double rayZ = z;

        double rayNormaliseScale = invSqr(dirX* dirX + dirY* dirY + dirZ * dirZ);

        double dx = dirX * rayNormaliseScale;
        double dy = dirY * rayNormaliseScale;
        double dz = dirZ * rayNormaliseScale;


        boolean COLLISION = false;

        while (TRAVELLED_DISTANCE < MAX_RAY_DISTANCE){
            rayX = rayX + dx;
            rayY = rayY + dy;
            rayZ = rayZ + dz;
            TRAVELLED_DISTANCE += 1;

            //if (rayY > 2){
            //    COLLISION = true;
            //    break;

            //}

            if (Math.sqrt(rayX*rayX+ rayY * rayY + (rayZ-8) * (rayZ-8)) - 4 < 0){
                COLLISION = true;
                break;
            }
        }

        if (COLLISION == false){
            return -1;
        }

        return TRAVELLED_DISTANCE;

    }

    public void renderRays() {
        for (int x = 0; x < H_VISIBLE; x++) {
            for (int y = 0; y < V_VISIBLE; y++) {
                int dis = runRay(0,0,0, x-(H_VISIBLE >> 1), y - (V_VISIBLE >> 1), 128);
                if (dis == -1){
                    setPixelRGB(x, y, 0, 255, 0);
                } else {
                    setPixelRGB(x, y, dis, 0, 0);
                }
            }
        }
    }

    public void fillColor(int r, int g, int b){
        for (int x = 0; x<H_VISIBLE; x++){
            for (int y = 0; y < V_VISIBLE; y++) {
                setPixelRGB(x, y, r, g, b);
            }
        }
    }

    public void setPixelRGB(int x, int y, int r, int g, int b){
        setPixel(x,y, (r << 16) | (g << 8) | b);
    }

    public void setPixel(int x, int y, int col){
        colorBuffer[x+(y*H_VISIBLE)] = col | 0xff_000000;
    }

    private void updateScreen() {
        pixelBuffer.updateBuffer(buffer -> null);
    }

    @Override
    protected void makeWindow() {
        super.makeWindow();

        ScreenWindow screen = new ScreenWindow(pixelBuffer);

        VBox root = new VBox(Cat65.SPACING, settings, screen);
        root.setId("root");

        Scene scene = new Scene(root);
        EmuHelper.applyCSS(scene);

        window.setTitle("GC3D Video Card [$%x000] >:3".formatted(portNum));
        window.setScene(scene);
        window.setResizable(false);
        window.setAlwaysOnTop(true);
    }
}
