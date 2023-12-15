package back_end.Mips.AsmInstrs;

import back_end.Mips.MipsBuilder;

public class MipsInstr {
    protected final String opcode;

    protected MipsInstr(String opcode) {
        MipsBuilder.MB.addInstr(this);
        this.opcode = opcode;
    }
}
