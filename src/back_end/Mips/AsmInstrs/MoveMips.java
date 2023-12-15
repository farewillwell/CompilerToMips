package back_end.Mips.AsmInstrs;

import back_end.Mips.Register;

public class MoveMips extends MipsInstr {

    private final Register dstReg;

    private final Register srcReg;

    public MoveMips(Register dstReg, Register srcReg) {
        super("move");
        this.dstReg = dstReg;
        this.srcReg = srcReg;
    }

    @Override
    public String toString() {
        if (dstReg != srcReg) {
            return opcode + " " + dstReg + ", " + srcReg;
        } else {
            return "# same reg " + dstReg;
        }
    }
}
