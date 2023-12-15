package back_end.Mips.AsmInstrs;

import back_end.Mips.Register;

public class CmpMips extends MipsInstr {
    public static final String SGE = "sge";
    public static final String SNE = "sne";
    public static final String SLT = "slt";
    public static final String SLE = "sle";
    public static final String SEQ = "seq";
    public static final String SGT = "sgt";

    private final Register ansRd;

    private final Register signLeftRegRs;
    private final Register signRightRegRt;

    public CmpMips(String opcode, Register ansRd, Register signLeftRegRs, Register signRightRegRt) {
        super(opcode);
        this.ansRd = ansRd;
        this.signLeftRegRs = signLeftRegRs;
        this.signRightRegRt = signRightRegRt;
    }

    @Override
    public String toString() {
        return opcode + " " + ansRd + ", " + signLeftRegRs + ", " + signRightRegRt;
    }
}
