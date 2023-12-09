package mid_end.llvm_ir.Instrs;

import back_end.Mips.AsmInstrs.MemAsm;
import back_end.Mips.AsmInstrs.MoveAsm;
import back_end.Mips.MipsBuilder;
import back_end.Mips.MipsSymbol;
import back_end.Mips.Register;
import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.LocalVar;
import mid_end.llvm_ir.Value;
import mid_end.llvm_ir.type.BaseType;

public class ZextInstr extends Instr {
    // extend 专门用于扩展,压缩的可以用icmp和0来表示

    public ZextInstr(BaseType targetType, Value beChangedValue) {
        addValue(beChangedValue);
        setAns(new LocalVar(targetType, false));
    }

    @Override
    public String toString() {
        return getAns() + " = zext " + paras.get(0).type + " " + paras.get(0) + " to " + getAns().type;
    }

    @Override
    public void genMipsCode() {
        super.genMipsCode();
        // 考虑到mips中没有i1,因此该指令就相当于move
        Register reg1 = Instr.moveValueIntoReg(Register.T0, paras.get(0));
        Register register = targetSRegorNull(getAns());
        if (register != null) {
            new MoveAsm(register, reg1);
            MipsBuilder.MB.storeInReg(getAns(), register);
        } else {
            Instr.storeMemFromReg(reg1, getAns());
        }
    }

    public boolean foldSelf() {
        if (paras.get(0).type == BaseType.I32) {
            getAns().userReplaceMeWith(paras.get(0));
            return true;
        }
        return false;
    }


}
