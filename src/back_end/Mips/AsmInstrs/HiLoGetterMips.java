package back_end.Mips.AsmInstrs;

import back_end.Mips.Register;

public class HiLoGetterMips extends MipsInstr {
    public static final String MFHI = "mfhi";
    public static final String MFLO = "mflo";
    private final Register ansRd;

    public HiLoGetterMips(String opcode, Register ansRd) {
        super(opcode);
        this.ansRd = ansRd;
    }

    @Override
    public String toString() {
        return opcode + " " + ansRd;
    }
}
