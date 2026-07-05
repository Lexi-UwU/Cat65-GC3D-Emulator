package org.kittykat.cat65.ui.window;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kittykat.cat65.EmuHelper;
import org.kittykat.cat65.core.CMU;

public class HexView extends WindowWithTitle {
    private final ObservableList<String> lines = FXCollections.observableArrayList();
    private final ListView<String> hexView;

    private static final double MAX_FONT_SIZE      = 12d;
    private static final double FONT_SIZE_CONSTANT = MAX_FONT_SIZE / 560d;
    private static final double MAX_CELL_SIZE      = 20d;
    private static final double FONT_TO_CELL_SIZE  = MAX_CELL_SIZE / MAX_FONT_SIZE;
    private final DoubleProperty fontSize = new SimpleDoubleProperty(MAX_FONT_SIZE);

    public HexView() {
        super("Hex Viewer [CPU Memory]");

        hexView = new ListView<>(lines);
        hexView.setCache(true);
        //hexView.setCacheHint(CacheHint.SPEED);
        VBox.setVgrow(hexView, Priority.ALWAYS);

        for (int l = 0x000; l <= 0xfff; l++) {
            lines.add(getVisualizerLine(l));
        }
        this.getChildren().add(hexView);

        hexView.setCellFactory(list -> new ListCell<>() {
            {
                setFontSize(fontSize.doubleValue());
                fontSize.addListener((obs, oldSize, newSize) -> setFontSize(newSize.doubleValue()));
            }

            private void setFontSize(double size) {
                setStyle("-fx-font-size: %f;".formatted(size));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
            }
        });
        hexView.widthProperty().addListener(
                (obs, oldWidth, newWidth) -> updateFontSize(newWidth.doubleValue()));
        updateFontSize(hexView.getWidth());
    }

    private void updateFontSize(double width) {
        double newSize = Math.min(MAX_FONT_SIZE, width * FONT_SIZE_CONSTANT);
        hexView.setFixedCellSize(Math.ceil(newSize * FONT_TO_CELL_SIZE));
        fontSize.set(newSize);
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

        int first = flow.getFirstVisibleCell().getIndex();
        int last  = flow.getLastVisibleCell().getIndex();

        for (int line = first; line <= last; line++) {
            updateVisualizerLine(line);
        }
    }
}
