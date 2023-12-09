package mid_end.llvm_ir.Instrs.IO;

import back_end.Mips.AsmInstrs.LiAsm;
import back_end.Mips.AsmInstrs.MoveAsm;
import back_end.Mips.AsmInstrs.Syscall;
import back_end.Mips.Register;
import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.Value;

public class PutInt extends IOInstr {
    public PutInt(Value value) {
        super();
        addValue(value);
    }

    public static final String defineHead = "declare void @putint(i32)\n";

    @Override
    public String toString() {
        return "call void @putint(" + paras.get(0).type + " " + paras.get(0).toString() + ")";
    }

    @Override
    public void genMipsCode() {
        super.genMipsCode();
        Register register = Instr.moveValueIntoReg(Register.A0, paras.get(0));
        if (register != Register.A0) {
            new MoveAsm(Register.A0, register);
        }
        new LiAsm(1, Register.V0);
        new Syscall();
    }

    @Override
    public Value getAns() {
        return null;
    }
}
