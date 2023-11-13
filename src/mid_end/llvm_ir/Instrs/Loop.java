package mid_end.llvm_ir.Instrs;

import mid_end.llvm_ir.BasicBlock;
import mid_end.llvm_ir.Value;

public class Loop extends Value {
    public BasicBlock condBlock;
    public BasicBlock loopBodyBlock;
    public BasicBlock loopEndInstrBlock;
    public BasicBlock afterLoopBlock;

    public Loop(BasicBlock condBlock,
                BasicBlock loopBodyBlock,
                BasicBlock loopEndInstrBlock,
                BasicBlock afterLoopBlock) {
        this.condBlock = condBlock;
        this.loopBodyBlock = loopBodyBlock;
        this.loopEndInstrBlock = loopEndInstrBlock;
        this.afterLoopBlock = afterLoopBlock;
    }

}
