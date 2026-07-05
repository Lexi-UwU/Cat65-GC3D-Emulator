package org.kittykat.cat65.ui.window.charts;

public class DataPointException extends RuntimeException {
    public DataPointException(String message) {
        super(message);
    }
    public DataPointException(int expectedCount, int receivedCount) {
        this("Expected %d Data Point(s), got %d instead!".formatted(expectedCount, receivedCount));
    }
}
