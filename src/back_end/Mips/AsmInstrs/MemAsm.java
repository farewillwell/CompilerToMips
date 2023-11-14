package back_end.Mips.AsmInstrs;

import back_end.Mips.Register;

public class MemAsm extends AsmInstr {
    public static final String LW = "lw";
    public static final String SW = "sw";


    private final Register rt;

    private final Register base;

    private final Integer offset;

    public MemAsm(String opcode, Register rt, Register base, Integer offset) {
        super(opcode);
        this.rt = rt;
        this.base = base;
        this.offset = offset;
    }
}
