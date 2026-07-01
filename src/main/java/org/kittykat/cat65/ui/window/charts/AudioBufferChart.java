package org.kittykat.cat65.ui.window.charts;

import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import org.kittykat.cat65.ArraySizeException;
import org.kittykat.cat65.core.CMU;

public class AudioBufferChart extends DebugChart {
    private static final int SAMPLE_COUNT = CMU.READ_BUFFER_SIZE * 2;
    private static final int TICK_UNIT    = CMU.READ_BUFFER_SIZE / 4;

    private final XYChart.Series<Number, Number> audioSeries = new XYChart.Series<>();

    public AudioBufferChart() {
        super(SAMPLE_COUNT, TICK_UNIT, -1d, 1d, .25d);
        getData().add(audioSeries);
        setDebugLine(SAMPLE_COUNT / 2);
        getStyleClass().add("debug-audio-chart");
    }

    public void set(float[] samples) {
        if (samples.length != SAMPLE_COUNT) {
            throw new ArraySizeException(SAMPLE_COUNT, samples.length);
        }
        ObservableList<XYChart.Data<Number, Number>> series = audioSeries.getData();
        series.clear();
        for (int s = 0; s < SAMPLE_COUNT; s++) {
            addPoint(series, s, samples[s]);
        }
    }
}
