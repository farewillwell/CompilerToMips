package mid_end.llvm_ir;

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

    @Override
    public void genMipsCode() {
        // 这个loop的过程已经被branch和jump拆解了,因此不必要生成
    }
}
