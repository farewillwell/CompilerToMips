package back_end.Mips.AsmInstrs;

import back_end.Mips.Register;

public class MemAsm extends AsmInstr {
    public static final String LW = "lw";
    public static final String SW = "sw";

    private final boolean useBase;

    // mode  ==1  : lw ,rt,offset(base)
    // mode ==2   : lw ,rt,label+offset


    private final Register rt;

    private final Register base;

    private final Integer offset;

    private final String label;

    public MemAsm(String opcode, Register rt, Register base, Integer offset) {
        super(opcode);
        this.rt = rt;
        this.base = base;
        this.label = null;
        this.offset = offset;
        this.useBase = true;
    }

    public MemAsm(String opcode, Register rt, String label, Integer offset) {
        super(opcode);
        this.rt = rt;
        this.base = null;
        this.label = label;
        this.offset = offset;
        this.useBase = false;
    }

    @Override
    public String toString() {
        if (useBase) {
            return opcode + " " + rt + ", " + offset + "(" + base + ")";
        } else {
            return opcode + " " + rt + ", " + label + "+" + offset;
        }
    }
}
