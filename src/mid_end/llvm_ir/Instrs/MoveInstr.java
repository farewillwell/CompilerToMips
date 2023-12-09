package mid_end.llvm_ir.Instrs;

import back_end.Mips.AsmInstrs.MoveAsm;
import back_end.Mips.MipsBuilder;
import back_end.Mips.Register;
import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.LocalVar;
import mid_end.llvm_ir.Value;

import java.util.HashSet;

public class MoveInstr extends Instr {

    private Value target() {
        return getAns();
    }

    private Value moveIn() {
        return paras.get(0);
    }

    public MoveInstr(Value target, Value moveIn) {
        setAns(target);
        addValue(moveIn);
    }

    @Override
    public String toString() {
        return "move " + target() + ", " + moveIn();
    }

    @Override
    public void genMipsCode() {
        super.genMipsCode();
        Register moveIn = Instr.moveValueIntoReg(Register.T0, moveIn());
        Register register = targetSRegorNull(target());
        if (register != null) {
            new MoveAsm(register, moveIn);
            MipsBuilder.MB.storeInReg(getAns(), register);
        } else {
            Instr.storeMemFromReg(moveIn, target());
        }
    }

    public boolean useUndefine(HashSet<Value> define) {
        if (moveIn() instanceof LocalVar) {
            return !define.contains(moveIn());
        }
        return false;
    }
}
