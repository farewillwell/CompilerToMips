package back_end.Mips.AsmInstrs;

import back_end.Mips.Register;

public class MulDivMips extends MipsInstr {
    public static final String MUL = "mult";
    public static final String DIV = "div";
    // 商放在lo 余数放在 hi
    private final Register regRs;
    private final Register regRt;

    public MulDivMips(String opcode, Register regRs, Register regRt) {
        super(opcode);
        this.regRs = regRs;
        this.regRt = regRt;
    }

    @Override
    public String toString() {
        return opcode + " " + regRs + ", " + regRt;
    }
}
