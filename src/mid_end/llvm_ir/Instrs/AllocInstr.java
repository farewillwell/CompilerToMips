package mid_end.llvm_ir.Instrs;

import back_end.Mips.AsmInstrs.AluR2IAsm;
import back_end.Mips.MipsBuilder;
import back_end.Mips.Register;
import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.LocalVar;
import mid_end.llvm_ir.type.LLVMType;
import mid_end.llvm_ir.type.PointerType;

public class AllocInstr extends Instr {

    public AllocInstr(LLVMType type, boolean isConst) {
        super(type);
        setAns(new LocalVar(new PointerType(type), isConst));
    }

    @Override
    public String toString() {
        return getAns().toString() + " = alloca " + type.toString();
    }

    @Override
    public void genMipsCode() {
        // 仅有非全局变量才需要alloca，很好
        super.genMipsCode();
        int offset = MipsBuilder.MB.allocOnStack(type.getSize());
        Register register = targetSRegorNull(getAns());
        if (register != null) {
            new AluR2IAsm(AluR2IAsm.ADDI, register, Register.SP, offset);
            MipsBuilder.MB.storeInReg(getAns(), register);
        } else {
            new AluR2IAsm(AluR2IAsm.ADDI, Register.T0, Register.SP, offset);
            Instr.storeMemFromReg(Register.T0, getAns());
        }
    }
}
