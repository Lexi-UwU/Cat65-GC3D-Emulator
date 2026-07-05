package org.kittykat.cat65.core.cpu;

import java.util.function.Consumer;
import java.util.function.IntSupplier;

class Opcode {
    public final Consumer<OpcodeContext> method;
    private final boolean hasInput;
    private final IntSupplier input;
    private final String args;
    public final int cycles;

    public Opcode(Consumer<OpcodeContext> method, IntSupplier input, String args, int cycles) {
        this.method = method;
        this.hasInput = (input != null);
        this.input = input;
        this.args = args;
        this.cycles = cycles;
    }

    public void execute() {
        int i = 0;
        if (hasInput) {
            i = input.getAsInt();
        }
        OpcodeContext c = new OpcodeContext(hasInput, i, args);
        method.accept(c);
    }

    @Override
    public String toString() {
        return "%s : %s \"%s\" (%d)".formatted(method, input, args, cycles);
    }
}
