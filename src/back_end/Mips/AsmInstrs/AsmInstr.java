package back_end.Mips.AsmInstrs;

import back_end.Mips.MipsBuilder;

public class AsmInstr {
    protected final String opcode;

    protected AsmInstr(String opcode) {
        MipsBuilder.MB.addInstr(this);
        this.opcode = opcode;
    }
}
