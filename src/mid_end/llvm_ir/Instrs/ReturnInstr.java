package mid_end.llvm_ir.Instrs;

import back_end.Mips.AsmInstrs.JumpAsm;
import back_end.Mips.AsmInstrs.LiAsm;
import back_end.Mips.AsmInstrs.MemAsm;
import back_end.Mips.MipsBuilder;
import back_end.Mips.Register;
import mid_end.llvm_ir.Constant;
import mid_end.llvm_ir.GlobalVar;
import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.Value;
import mid_end.llvm_ir.type.BaseType;

public class ReturnInstr extends Instr {
    private final BaseType retType;

    private Value getRetValue() {
        return paras.get(0);
    }

    public ReturnInstr(Value value) {
        super();
        retType = BaseType.I32;
        addValue(value);
    }

    public ReturnInstr() {
        retType = BaseType.Void;
    }

    @Override
    public String toString() {
        // StringBuilder builder =new StringBuilder();
        if (retType.equals(BaseType.I32)) {
            return "ret i32 " + getRetValue();
        } else {
            return "ret void";
        }
    }

    @Override
    public void genMipsCode() {
        super.genMipsCode();
        if (retType.equals(BaseType.I32)) {
            if (getRetValue() instanceof Constant) {
                new LiAsm(((Constant) getRetValue()).getValue(), Register.V0);
            } else if (getRetValue() instanceof GlobalVar) {
                new MemAsm(MemAsm.LW, Register.V0, ((GlobalVar) getRetValue()).nameInMips(), 0);
            } else {
                int offset = MipsBuilder.MB.queryOffset(getRetValue());
                new MemAsm(MemAsm.LW, Register.V0, Register.SP, offset);
            }
        }
        new JumpAsm(JumpAsm.JR, Register.RA);
    }
}
