package mid_end.llvm_ir.Instrs;

import back_end.Mips.AsmInstrs.BranchAsm;
import back_end.Mips.AsmInstrs.MemAsm;
import back_end.Mips.MipsBuilder;
import back_end.Mips.Register;
import mid_end.llvm_ir.BasicBlock;
import mid_end.llvm_ir.Constant;
import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.Value;

public class BranchInstr extends Instr {
    private BasicBlock thenBlock;
    private BasicBlock elseBlock;

    public Value getCond() {
        return paras.get(0);
    }

    public BasicBlock getThenBlock() {
        return thenBlock;
    }

    public BasicBlock getElseBlock() {
        return elseBlock;
    }

    @Override
    public Value getAns() {
        return null;
    }

    public BranchInstr(Value cond, BasicBlock thenBlock, BasicBlock elseBlock) {
        addValue(cond);
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
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
        Register cond= Instr.moveValueIntoReg(Register.T0, getCond());
        new BranchAsm(BranchAsm.BNE, cond, Register.ZERO, getThenBlock().nameInMips);
        new BranchAsm(BranchAsm.BEQ, cond, Register.ZERO, getElseBlock().nameInMips);
    }

    public void changeThen(BasicBlock newThen) {
        this.thenBlock = newThen;
    }

    public void changeElse(BasicBlock newElse) {
        this.elseBlock = newElse;
    }

    public boolean condConst() {
        return getCond() instanceof Constant;
    }

    public JumpInstr makeEqualJump() {
        if (((Constant) paras.get(0)).getValue() != 0) {
            return new JumpInstr(getThenBlock());
        } else {
            return new JumpInstr(getElseBlock());
        }
    }

    // 计算在常数化后被舍弃的块
    public BasicBlock abandonTarget() {
        if (((Constant) paras.get(0)).getValue() == 0) {
            return getThenBlock();
        } else {
            return getElseBlock();
        }
    }
}
