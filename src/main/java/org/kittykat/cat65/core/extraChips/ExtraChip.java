package org.kittykat.cat65.core.extraChips;

public abstract class ExtraChip {
    private final int bitMask;

    public ExtraChip(int mirrorAddressMask) {
        this.bitMask = mirrorAddressMask;
    }

    public void reset() {}
    public void clock() {}

    public boolean getIRQ() {
        return true;
    }

    public int read(int address, boolean cpu) {
        return (get(getRelativeAddress(address), cpu) & 0xff);
    }
    protected abstract int get(int relAddress, boolean cpu);

    public void write(int address, int value, boolean cpu) {
        set(getRelativeAddress(address), value & 0xff, cpu);
    }
    protected abstract void set(int relAddress, int value, boolean cpu);

    protected int getRelativeAddress(int address) {
        return (address & bitMask);
    }
}
