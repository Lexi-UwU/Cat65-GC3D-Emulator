package org.kittykat.cat65.core.cpu;

class OpcodeException extends RuntimeException {
    public OpcodeException(String message) {
        super(message);
    }
    public OpcodeException(int undefined) {
        this(String.format("Undefined opcode! ($%02x)", undefined));
    }
    public OpcodeException(int duplicate, Opcode A, Opcode B) {
        this(String.format("Duplicate opcode! ($%02x)\n%s\n%s", duplicate, A, B));
    }
}
