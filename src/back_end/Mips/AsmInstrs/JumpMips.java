package back_end.Mips.AsmInstrs;

import back_end.Mips.Register;

public class JumpMips extends MipsInstr {
    public static final String JAL = "jal";
    public static final String JR = "jr";
    public static final String J = "j";

    private final String dstLabel;
    private final Register dstReg;

    public JumpMips(String opcode, Register dstReg) {
        super(opcode);
        this.dstLabel = null;
        this.dstReg = dstReg;
    }

    public JumpMips(String opcode, String dstLabel) {
        super(opcode);
        this.dstLabel = dstLabel;
        this.dstReg = null;
    }

    @Override
    public String toString() {
        if (opcode.equals("jr")) {
            return opcode + " " + dstReg;
        } else {
            return opcode + " " + dstLabel;
        }
    }
}
