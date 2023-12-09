package mid_end.llvm_ir.Instrs;

import back_end.Mips.AsmInstrs.JumpAsm;
import back_end.Mips.AsmInstrs.MoveAsm;
import back_end.Mips.Register;
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
            Register get = Instr.moveValueIntoReg(Register.V0, getRetValue());
            if (get != Register.V0) {
                new MoveAsm(Register.V0, get);
            }
        }
        new JumpAsm(JumpAsm.JR, Register.RA);
    }

    @Override
    public Value getAns() {
        return null;
    }
}
