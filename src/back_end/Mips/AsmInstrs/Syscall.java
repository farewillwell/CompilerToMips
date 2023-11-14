package back_end.Mips.AsmInstrs;

public class Syscall extends AsmInstr {
    public Syscall() {
        super("syscall");
    }

    @Override
    public String toString() {
        return opcode;
    }
}
