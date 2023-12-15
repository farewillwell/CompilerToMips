package back_end.Mips.AsmInstrs;


import back_end.Mips.Register;

public class LaMips extends MipsInstr {
    private final String label;
    private final Register dstReg;

    public LaMips(String label, Register reg) {
        super("la");
        this.label = label;
        this.dstReg = reg;
    }

    @Override
    public String toString() {
        return opcode + " " + dstReg + ", " + label;
    }
}
