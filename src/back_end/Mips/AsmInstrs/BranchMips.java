package back_end.Mips.AsmInstrs;

import back_end.Mips.Register;

public class BranchMips extends MipsInstr {
    public static final String BEQ = "beq";

    public static final String BNE = "bne";

    private final Register regRs;

    private final Register regRt;

    private final String label;

    public BranchMips(String opcode, Register regRs, Register regRt, String label) {
        super(opcode);
        this.regRs = regRs;
        this.regRt = regRt;
        this.label = label;
    }

    @Override
    public String toString() {
        return opcode + " " + regRs + ", " + regRt + ", " + label;
    }
}
