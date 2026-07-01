package org.kittykat.cat65.ui.window;

import javafx.geometry.HPos;
import javafx.scene.layout.GridPane;
import org.kittykat.cat65.EmuHelper;
import org.kittykat.cat65.core.CMU;
import org.kittykat.cat65.ui.window.charts.AudioBufferChart;
import org.kittykat.cat65.ui.window.charts.DebugLineChart;

public class DebugWindow extends WindowWithTitle {
    private final DebugLineChart   clockSpeedChart  = new DebugLineChart(1_250_000,        250_000, 2);
    private final DebugLineChart   audioBufferChart = new DebugLineChart(CMU.SAMPLER_BUFFER_SIZE, 256,     2);
    private final AudioBufferChart audioOutputChart = new AudioBufferChart();

    public DebugWindow() {
        super("Debug Info");
        GridPane content = EmuHelper.makeGrid(2, 2, HPos.CENTER);
        content.addRow(0, audioBufferChart, audioOutputChart);
        content.addRow(1, clockSpeedChart);
        getChildren().add(content);
    }

    @Override
    public void updateWindow() {
        clockSpeedChart.add( CMU.getTargetClockSpeed(), CMU.getCurrentClockSpeed());
        audioBufferChart.add(CMU.READ_BUFFER_SIZE,   CMU.getAudioBufferFillLevel());
        audioOutputChart.set(CMU.debug_audioBuffer);
    }
}
