package back_end.Mips.AsmInstrs;

import back_end.Mips.Register;

public class LiAsm extends AsmInstr {
    private final Register dstReg;
    private final Integer value;
    // 这个可以加载32位的数字，不像addi顶多16位

    public LiAsm(Integer value, Register register) {
        super("li");
        this.dstReg = register;
        this.value = value;
    }

    @Override
    public String toString() {
        return opcode + " " + dstReg + ", " + value;
    }
}
