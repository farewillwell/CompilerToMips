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

    private final Value retValue;

    public ReturnInstr(Value value) {
        super();
        retType = BaseType.I32;
        retValue = value;
    }

    public ReturnInstr() {
        retType = BaseType.Void;
        retValue = null;
    }

    @Override
    public String toString() {
        // StringBuilder builder =new StringBuilder();
        if (retType.equals(BaseType.I32)) {
            assert retValue != null;
            return "ret i32 " + retValue;
        } else {
            return "ret void";
        }
    }

    @Override
    public void genMipsCode() {
        super.genMipsCode();
        if (retType.equals(BaseType.I32)) {
            if (retValue instanceof Constant) {
                new LiAsm(((Constant) retValue).getValue(), Register.V0);
            } else if (retValue instanceof GlobalVar) {
                new MemAsm(MemAsm.LW, Register.V0, ((GlobalVar) retValue).nameInMips(), 0);
            } else {
                int offset = MipsBuilder.MB.queryOffset(retValue);
                new MemAsm(MemAsm.LW, Register.V0, Register.SP, offset);
            }
        }
        new JumpAsm(JumpAsm.JR, Register.RA);
    }
}
