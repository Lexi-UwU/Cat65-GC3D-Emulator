package org.kittykat.cat65.core.cpu;

import java.util.function.Consumer;
import java.util.function.IntSupplier;

record OpcodeDef(IntSupplier input, int cycles, int[] opcodes, String args) {}
record OpcodeType(Consumer<OpcodeContext> method, OpcodeDef[] opcodeDefinitions) {}

class OpcodeContext {
    public boolean hasInput;
    public int input;
    public String args;

    public OpcodeContext(boolean hasInput, int input, String args) {
        this.hasInput = hasInput;
        this.input = input;
        this.args = args;
    }
}
