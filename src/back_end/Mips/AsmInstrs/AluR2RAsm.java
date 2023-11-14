package back_end.Mips.AsmInstrs;

import back_end.Mips.Register;

public class AluR2RAsm extends AsmInstr {
    public static final String ADD = "add";
    public static final String SUB = "sub";

    public static final String OR = "or";
    public static final String AND = "and";
    private final Register ansRd;
    private final Register opRegRs;

    private final Register opRegRt;

    public AluR2RAsm(String opcode, Register ansRd, Register opRegRs, Register opRegRt) {
        super(opcode);
        this.ansRd = ansRd;
        this.opRegRs = opRegRs;
        this.opRegRt = opRegRt;
    }
    @Override
    public String toString() {
        return opcode + " " + ansRd + ", " + opRegRs + ", " +opRegRt ;
    }
}
