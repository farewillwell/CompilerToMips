package back_end.Mips.AsmInstrs;

import back_end.Mips.Register;

public class MoveAsm extends AsmInstr {

    private final Register dstReg;

    private final Register srcReg;

    public MoveAsm(Register dstReg, Register srcReg) {
        super("move");
        this.dstReg = dstReg;
        this.srcReg = srcReg;
    }

    @Override
    public String toString() {
        return opcode + " " + dstReg + ", " + srcReg;
    }
}
