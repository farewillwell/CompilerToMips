package mid_end.llvm_ir.Instrs;

import mid_end.llvm_ir.BasicBlock;
import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.Value;

public class BranchInstr extends Instr {
    public boolean needCond;
    private Value condValue;
    private BasicBlock destTo;
    private BasicBlock ifBlock;
    private BasicBlock elseBlock;

    public BranchInstr(Value cond,BasicBlock ifBlock)
    {
        this.needCond=true;
        this.condValue=cond;
        this.ifBlock=ifBlock;
        this.destTo=null;
        this.elseBlock=null;

    }
    public BranchInstr(Value cond,BasicBlock ifBlock ,BasicBlock elseBlock)
    {
        this.needCond=true;
        this.condValue=cond;
        this.destTo=null;
        this.ifBlock=ifBlock;
        this.elseBlock=elseBlock;
    }
    public BranchInstr(BasicBlock destTo)
    {
        this.needCond=false;
        this.condValue=null;
        this.destTo=destTo;
        this.ifBlock=null;
        this.elseBlock=null;
    }
}
