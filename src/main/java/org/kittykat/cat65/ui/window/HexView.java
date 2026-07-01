package org.kittykat.cat65.ui.window;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    public HexView() {
        super("Hex Viewer [CPU Memory]");

        hexView = new ListView<>(lines);
        hexView.setFixedCellSize(20);
        hexView.setCache(true);
        //hexView.setCacheHint(CacheHint.SPEED);
        VBox.setVgrow(hexView, Priority.ALWAYS);

        for (int l = 0x000; l <= 0xfff; l++) {
            lines.add(getVisualizerLine(l));
        }
        this.getChildren().add(hexView);
    }

    public String getVisualizerLine(int lineNum) {
        int address = lineNum << 4;
        StringBuilder hex = new StringBuilder(String.format("$%04x:", address));
        StringBuilder ascii = new StringBuilder("  |  ");
        for (int a = 0; a < 16; a++) {
            int b = CMU.get(address + a, false);
            hex.append(String.format("%s %02x", (a == 8) ? " " : "", b));
            char chr = EmuHelper.convertToASCII(b);
            ascii.append(String.format("%s%c", (a == 8) ? " " : "", chr));
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
