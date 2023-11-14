package back_end.Mips.AsmInstrs;

import back_end.Mips.Register;

public class MulDivAsm extends AsmInstr {
    private final Register regRs;
    private final Register regRt;

    public MulDivAsm(String opcode, Register regRs, Register regRt) {
        super(opcode);
        this.regRs = regRs;
        this.regRt = regRt;
    }

    @Override
    public String toString() {
        return opcode + " " + regRs + ", " + regRt;
    }
}
