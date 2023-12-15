package mid_end.llvm_ir.Instrs.IO;

import back_end.Mips.AsmInstrs.LiMips;
import back_end.Mips.AsmInstrs.Syscall;
import back_end.Mips.Register;
import mid_end.llvm_ir.Constant;
import mid_end.llvm_ir.Value;

public class PutCh extends IOInstr {

    public PutCh(Value value) {
        super();
        addValue(value);
    }

    @Override
    public Value getAns() {
        return null;
    }

    public static final String defineHead = "declare void @putch(i32)\n";

    @Override
    public String toString() {
        return "call void @putch(" + paras.get(0).type + " " + paras.get(0) + ")";
    }

    @Override
    public void genMipsCode() {
        super.genMipsCode();
        new LiMips(((Constant) paras.get(0)).getValue(), Register.A0);
        new LiMips(11, Register.V0);
        new Syscall();
    }
}
