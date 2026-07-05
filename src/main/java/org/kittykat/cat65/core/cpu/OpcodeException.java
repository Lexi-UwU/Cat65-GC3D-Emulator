package org.kittykat.cat65.core.cpu;

class OpcodeException extends RuntimeException {
    public OpcodeException(String message) {
        super(message);
    }
    public OpcodeException(int undefined) {
        this("Undefined opcode! ($%02x)".formatted(undefined));
    }
    public OpcodeException(int duplicate, Opcode A, Opcode B) {
        this("Duplicate opcode! ($%02x)\n%s\n%s".formatted(duplicate, A, B));
    }
}
