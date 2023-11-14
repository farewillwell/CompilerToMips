package back_end.Mips.AsmInstrs;


import back_end.Mips.Register;

public class LaAsm extends AsmInstr {
    private final String label;
    private final Register dstReg;

    public LaAsm(String label, Register reg) {
        super("la");
        this.label = label;
        this.dstReg = reg;
    }

    @Override
    public String toString() {
        return opcode + " " + dstReg + ", " + label;
    }
}
