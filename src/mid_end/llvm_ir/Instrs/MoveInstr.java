package mid_end.llvm_ir.Instrs;

import back_end.Mips.AsmInstrs.MemAsm;
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
        // 一个难点，就是move会出现给多个move赋值的情况，那么每次做的时候，
        // 假如已经分配值了，就返回这个offset，否则新分配一个值返回
        int offset = MipsBuilder.MB.queryOffset(target());
        Instr.getValueInReg(Register.T0, moveIn());
        new MemAsm(MemAsm.SW, Register.T0, Register.SP, offset);
    }

    public boolean useUndefine(HashSet<Value> define) {
        if (moveIn() instanceof LocalVar) {
            return !define.contains(moveIn());
        }
        return false;
    }
}
