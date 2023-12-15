package back_end.Mips.AsmInstrs;

import back_end.Mips.Register;

public class AluR2IMips extends MipsInstr {
    public static final String ADDI = "addi"; //  可以存32位的立即数，就可以拆作li + add
    public static final String SLL = "sll";
    private final Register ansRd;
    private final Register opRegRt;

    private final Integer imm;

    public AluR2IMips(String opcode, Register ansRd, Register opRegRt, Integer imm) {
        super(opcode);
        this.ansRd = ansRd;
        this.opRegRt = opRegRt;
        this.imm = imm;
    }

    @Override
    public String toString() {
        return opcode + " " + ansRd + ", " + opRegRt + ", " + imm;
    }
}

