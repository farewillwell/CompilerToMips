package mid_end.llvm_ir.Instrs.IO;

import back_end.Mips.AsmInstrs.LiAsm;
import back_end.Mips.AsmInstrs.MemAsm;
import back_end.Mips.AsmInstrs.Syscall;
import back_end.Mips.MipsBuilder;
import back_end.Mips.MipsSymbol;
import back_end.Mips.Register;
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
        new LiAsm(5, Register.V0);
        new Syscall();
        int offset = MipsBuilder.MB.allocOnStack(getAns().type.getSize());
        MipsBuilder.MB.addVarSymbol(new MipsSymbol(getAns(),offset));
        new MemAsm(MemAsm.SW, Register.V0, Register.SP, offset);
    }
    // 这个玩意都有bug？什么玩意 因为读入的整数是存到V0里了，而不是a0!!!
}
