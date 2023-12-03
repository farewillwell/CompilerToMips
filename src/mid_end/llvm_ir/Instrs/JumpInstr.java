package mid_end.llvm_ir.Instrs;

import back_end.Mips.AsmInstrs.JumpAsm;
import mid_end.llvm_ir.BasicBlock;
import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.Value;

public class JumpInstr extends Instr {

    private BasicBlock dstBlock;

    public JumpInstr(BasicBlock dstBlock) {
        this.dstBlock = dstBlock;
    }

    @Override
    public String toString() {
        return "br label %" + dstBlock.name;
    }

    @Override
    public Value getAns() {
        return null;
    }

    @Override
    public void genMipsCode() {
        super.genMipsCode();
        new JumpAsm(JumpAsm.J, dstBlock.nameInMips);
    }

    public BasicBlock getTargetBlock() {
        return dstBlock;
    }

    public void setDstBlock(BasicBlock dstBlock) {
        this.dstBlock = dstBlock;
    }
}
