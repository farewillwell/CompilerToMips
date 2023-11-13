package mid_end.llvm_ir.Instrs;

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

    @Override
    public String toString() {
        return "br " + getCond().type + " " + getCond() +
                ", label " + getThenBlock().name + ", label " + getElseBlock().name;
    }
}
