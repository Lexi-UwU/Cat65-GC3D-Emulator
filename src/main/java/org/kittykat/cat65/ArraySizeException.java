package org.kittykat.cat65;

public class ArraySizeException extends RuntimeException {
    public ArraySizeException(String message) {
        super(message);
    }
    public ArraySizeException(int expected, int received) {
        this("Expected an Array of size %d, got one of size %d instead!".formatted(expected, received));
    }
}
