package mid_end.llvm_ir.Instrs.IO;

import back_end.Mips.AsmInstrs.LiMips;
import back_end.Mips.AsmInstrs.MoveMips;
import back_end.Mips.AsmInstrs.Syscall;
import back_end.Mips.MipsBuilder;
import back_end.Mips.Register;
import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.LocalVar;
import mid_end.llvm_ir.type.BaseType;

public class GetInt extends IOInstr {

    public GetInt() {
        setAns(new LocalVar(BaseType.I32, false));
    }

    public static final String defineHead = "declare i32 @getint()\n";

    @Override
    public String toString() {
        return getAns().toString() + " = call i32 @getint()";
    }

    @Override
    public void genMipsCode() {
        super.genMipsCode();
        new LiMips(5, Register.V0);
        new Syscall();
        Register register = targetSRegorNull(getAns());
        if (register != null) {
            new MoveMips(register, Register.V0);
            MipsBuilder.MB.storeInReg(getAns(), register);
        } else {
            Instr.storeMemFromReg(Register.V0, getAns());
        }
    }
    // 这个玩意都有bug？什么玩意 因为读入的整数是存到V0里了，而不是a0!!!
}
