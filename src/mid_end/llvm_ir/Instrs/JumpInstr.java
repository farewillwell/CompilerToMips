package mid_end.llvm_ir.Instrs;

import mid_end.llvm_ir.BasicBlock;
import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.Value;

public class JumpInstr extends Instr {
    public JumpInstr(BasicBlock dstBlock) {
        addValue(dstBlock);
    }

    @Override
    public String toString() {
        return "br label " + ((BasicBlock) paras.get(0)).name;
    }

    @Override
    public Value getAns() {
        throw new RuntimeException("use ans of a jump instr");
    }
}
