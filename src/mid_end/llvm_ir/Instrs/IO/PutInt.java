package mid_end.llvm_ir.Instrs.IO;

import back_end.Mips.AsmInstrs.LiAsm;
import back_end.Mips.AsmInstrs.MemAsm;
import back_end.Mips.AsmInstrs.Syscall;
import back_end.Mips.MipsBuilder;
import back_end.Mips.Register;
import mid_end.llvm_ir.Constant;
import mid_end.llvm_ir.GlobalVar;
import mid_end.llvm_ir.Value;

public class PutInt extends IOInstr {
    public PutInt(Value value) {
        super();
        paras.add(value);
    }

    public static final String defineHead = "declare void @putint(i32)\n";

    @Override
    public String toString() {
        return "call void @putint(" + paras.get(0).type + " " + paras.get(0).toString() + ")";
    }

    @Override
    public void genMipsCode() {
        super.genMipsCode();
        if (paras.get(0) instanceof Constant) {
            new LiAsm(((Constant) paras.get(0)).getValue(), Register.A0);
        } else if (paras.get(0) instanceof GlobalVar) {
            new MemAsm(MemAsm.LW, Register.A0, ((GlobalVar) paras.get(0)).name, 0);
        } else {
            int offset = MipsBuilder.MB.queryOffset(paras.get(0));
            new MemAsm(MemAsm.LW, Register.A0, Register.SP, offset);
        }
        new LiAsm(1, Register.V0);
        new Syscall();
    }
}
