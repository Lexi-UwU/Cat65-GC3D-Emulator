package org.kittykat.cat65.core;

public class RingBuffer {
    private final int     bufferSize;
    private final float[] buffer;
    private volatile int  readPos  = 0;
    private volatile int  writePos = 0;

    public RingBuffer(int bufferSize) {
        this.bufferSize = bufferSize;
        this.buffer = new float[bufferSize];
    }

    @SuppressWarnings("NonAtomicOperationOnVolatileField")
    public float read() {
        float value = 0f;
        if (readPos != writePos) {
            value   = buffer[readPos];
            readPos = (readPos + 1) % bufferSize;
        }
        return value;
    }
    public float[] readBuffer(int length) {
        float[] buffer = new float[length];
        for (int i = 0; i < length; i++) {
            buffer[i] = read();
        }
        return buffer;
    }

    public void write(float value) {
        int nextWrite = (writePos + 1) % bufferSize;
        if (nextWrite != readPos) {
            buffer[writePos] = value;
            writePos = nextWrite;
        }
    }

    public int length() {
        int len = writePos - readPos;
        if (len < 0) len += bufferSize;
        return len;
    }
}
