package front_end.AST.Stmt;

import front_end.AST.Exp.Cond;
import front_end.ErrorCollector;
import mid_end.llvm_ir.BasicBlock;
import mid_end.llvm_ir.IRBuilder;
import mid_end.llvm_ir.Instrs.JumpInstr;
import mid_end.llvm_ir.Value;

public class IfStmt extends Stmt {
    private final Cond cond;
    private final Stmt lStmt;

    private final Stmt rStmt;

    public IfStmt(Cond cond, Stmt lStmt) {
        this.cond = cond;
        this.lStmt = lStmt;
        rStmt = null;
    }

    public IfStmt(Cond cond, Stmt lStmt, Stmt rStmt) {
        this.cond = cond;
        this.lStmt = lStmt;
        this.rStmt = rStmt;
    }

    @Override
    public void show() {
        System.out.println("IFTK if");
        System.out.println("LPARENT (");
        cond.show();
        System.out.println("RPARENT )");
        lStmt.show();
        if (rStmt != null) {
            System.out.println("ELSETK else");
            rStmt.show();
        }
        super.show();
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        cond.checkError(errorCollector);
        lStmt.checkError(errorCollector);
        if (rStmt != null) {
            rStmt.checkError(errorCollector);
        }
    }

    @Override
    public Value getIRCode() {
        if (rStmt != null) {
            BasicBlock thenBlock = new BasicBlock();
            // thenBlock 是 如果if生效的话要做的块
            BasicBlock elseBlock = new BasicBlock();
            // elseBlock 是 如果if失效要做的块
            // 无论走哪个分支，走完后都要做的块
            cond.genCondIr(thenBlock, elseBlock);
            IRBuilder.IB.enterBlock(thenBlock);
            lStmt.getIRCode();
            BasicBlock finallyBlock = new BasicBlock();
            JumpInstr jumpOfIf = new JumpInstr(finallyBlock);
            IRBuilder.IB.addInstrForBlock(jumpOfIf);
            IRBuilder.IB.enterBlock(elseBlock);
            rStmt.getIRCode();
            JumpInstr jumpOfElse = new JumpInstr(finallyBlock);
            IRBuilder.IB.addInstrForBlock(jumpOfElse);
            IRBuilder.IB.enterBlock(finallyBlock);
        } else {
            BasicBlock thenBlock = new BasicBlock();
            // thenBlock 是 如果if生效的话要做的块
            BasicBlock finallyBlock = new BasicBlock();
            // 不走if要走的块或者走完if要做的块
            cond.genCondIr(thenBlock, finallyBlock);
            IRBuilder.IB.enterBlock(thenBlock);
            lStmt.getIRCode();
            JumpInstr jumpOfIf = new JumpInstr(finallyBlock);
            IRBuilder.IB.addInstrForBlock(jumpOfIf);
            IRBuilder.IB.enterBlock(finallyBlock);
        }
        return null;
    }
}
