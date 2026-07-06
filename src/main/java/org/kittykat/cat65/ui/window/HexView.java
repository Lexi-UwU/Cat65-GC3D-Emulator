package org.kittykat.cat65.ui.window;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.CacheHint;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kittykat.cat65.EmuHelper;
import org.kittykat.cat65.core.CMU;

import java.util.Locale;
import java.util.logging.Logger;

public class HexView extends WindowWithTitle {
    private final ObservableList<String> lines = FXCollections.observableArrayList();
    private final ListView<String> hexView;

    private static final double MAX_FONT_SIZE      = 12d;
    private static final double FONT_SIZE_CONSTANT = MAX_FONT_SIZE / 560d;

    /*
     * JavaFX logs a bogus
     *   INFO: index exceeds maxCellCount. Check size calculations for class javafx.scene.control.skin.ListViewSkin$2
     * whenever a ListView is scrolled to its very end and VirtualFlow has to pad
     * the space below the last row with empty cells (this happens at window sizes
     * where the last row doesn't end exactly at the viewport bottom).
     * The sanity check in VirtualFlow.addTrailingCells compares the ABSOLUTE row
     * index (up to 0x1000 here) against the viewport height in PIXELS, so any list
     * with more rows than the viewport is pixels tall trips it. Known upstream bug
     * (JDK-8140504, still present in current OpenJFX master), harmless and
     * self-correcting - so we filter out exactly this one message.
     * Kept as a static field because LogManager only holds weak refs to loggers.
     */
    private static final Logger CONTROLS_LOGGER = Logger.getLogger("javafx.scene.control");
    static {
        CONTROLS_LOGGER.setFilter(record ->
                record.getMessage() == null || !record.getMessage().contains("index exceeds maxCellCount"));
    }

    public HexView() {
        super("Hex Viewer [CPU Memory]");

        hexView = new ListView<>(lines);
        hexView.setCache(true);
        hexView.setCacheHint(CacheHint.SPEED);
        VBox.setVgrow(hexView, Priority.ALWAYS);

        for (int l = 0x000; l <= 0xfff; l++) {
            lines.add(getVisualizerLine(l));
        }
        getChildren().add(hexView);

        hexView.widthProperty().addListener( (obs, oldWidth, newWidth) -> updateFontSize(newWidth.doubleValue()));
        updateFontSize(hexView.getWidth());
    }

    private void updateFontSize(double width) {
        double newSize = Math.min(MAX_FONT_SIZE, width * FONT_SIZE_CONSTANT);
        // Locale.ROOT: on decimal-comma locales (e.g. de-DE) "%.3f" yields "6,566"
        // and the CSS parser silently drops everything after the comma
        hexView.setStyle(String.format(Locale.ROOT, "-fx-font-size: %.3f;", newSize));
    }

    public String getVisualizerLine(int lineNum) {
        int address = lineNum << 4;
        StringBuilder hex = new StringBuilder("$%04x:".formatted(address));
        StringBuilder ascii = new StringBuilder("  |  ");
        for (int a = 0; a < 16; a++) {
            int b = CMU.get(address + a, false);
            hex.append("%s %02x".formatted((a == 8) ? " " : "", b));
            char chr = EmuHelper.convertToASCII(b);
            ascii.append("%s%c".formatted((a == 8) ? " " : "", chr));
        }
        return hex.toString() + ascii;
    }
    public void updateVisualizerLine(int lineNum) {
        lines.set(lineNum, getVisualizerLine(lineNum));
    }

    @Override
    public void updateWindow() {
        ListViewSkin<?> skin = (ListViewSkin<?>) hexView.getSkin();
        VirtualFlow<?> flow = (VirtualFlow<?>) skin.getChildren().getFirst();

        int first = Math.max(0x000, flow.getFirstVisibleCell().getIndex());
        int last  = Math.min(0xfff, flow.getLastVisibleCell().getIndex());

        for (int line = first; line <= last; line++) {
            updateVisualizerLine(line);
        }
    }
}
