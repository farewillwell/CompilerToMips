package mid_end.llvm_ir.Instrs;

import back_end.Mips.AsmInstrs.BranchAsm;
import back_end.Mips.AsmInstrs.MemAsm;
import back_end.Mips.MipsBuilder;
import back_end.Mips.Register;
import mid_end.llvm_ir.BasicBlock;
import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.Value;

public class BranchInstr extends Instr {
    public Value getCond() {
        return paras.get(0);
    }

    public BasicBlock getThenBlock() {
        return (BasicBlock) paras.get(1);
    }

    public BasicBlock getElseBlock() {
        return (BasicBlock) paras.get(2);
    }

    @Override
    public Value getAns() {
        throw new RuntimeException("use ans of a branch instr");
    }

    public BranchInstr(Value cond, Value thenBlock, Value elseBlock) {
        addValue(cond);
        addValue(thenBlock);
        addValue(elseBlock);
    }

    // 由于cond都是一位数，所以可以直接和0判断
    @Override
    public String toString() {
        return "br " + getCond().type + " " + getCond() +
                ", label %" + getThenBlock().name + ", label %" + getElseBlock().name;
    }
    // cond = 1则跳转，可以翻译成两条，不等于0那么就跳到lab1，等于0那么就跳到label2

    @Override
    public void genMipsCode() {
        super.genMipsCode();
        int offset =MipsBuilder.MB.queryOffset(paras.get(0));
        new MemAsm(MemAsm.LW,Register.T0,Register.SP,offset);
        new BranchAsm(BranchAsm.BNE, Register.T0, Register.ZERO, getThenBlock().nameInMips);
        new BranchAsm(BranchAsm.BEQ, Register.T0, Register.ZERO, getElseBlock().nameInMips);
    }
}
