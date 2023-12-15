package mid_end.llvm_ir.Instrs;

import back_end.Mips.AsmInstrs.JumpMips;
import back_end.Mips.AsmInstrs.MoveMips;
import back_end.Mips.Register;
import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.Value;
import mid_end.llvm_ir.type.BaseType;

public class ReturnIr extends Instr {
    private final BaseType retType;

    private Value getRetValue() {
        return paras.get(0);
    }

    public ReturnIr(Value value) {
        super();
        retType = BaseType.I32;
        addValue(value);
    }

    public ReturnIr() {
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
                new MoveMips(Register.V0, get);
            }
        }
        new JumpMips(JumpMips.JR, Register.RA);
    }

    @Override
    public Value getAns() {
        return null;
    }
}
