package front_end.AST.Stmt;

import front_end.AST.Exp.Cond;
import front_end.ErrUseSymbols.ErrUseSymbolManager;
import front_end.ErrorCollector;
import mid_end.llvm_ir.BasicBlock;
import mid_end.llvm_ir.IRBuilder;
import mid_end.llvm_ir.Instrs.JumpInstr;
import mid_end.llvm_ir.Instrs.Loop;
import mid_end.llvm_ir.Value;

public class BigForStmt extends Stmt {
    private front_end.AST.Exp.ForStmt forStmtLeft;

    private Cond cond;
    private front_end.AST.Exp.ForStmt forStmtRight;

    private Stmt stmt;

    public BigForStmt() {
        forStmtLeft = null;
        forStmtRight = null;
        cond = null;
        stmt = null;
    }

    public void addForExpLeft(front_end.AST.Exp.ForStmt forStmt) {
        this.forStmtLeft = forStmt;
    }

    public void addForExpRight(front_end.AST.Exp.ForStmt forStmt) {
        this.forStmtRight = forStmt;
    }

    public void addCond(Cond cond) {
        this.cond = cond;
    }

    public void addStmt(Stmt stmt) {
        this.stmt = stmt;
    }

    @Override
    public void show() {
        System.out.println("FORTK for");
        System.out.println("LPARENT (");
        if (forStmtLeft != null) {
            forStmtLeft.show();
        }
        System.out.println("SEMICN ;");
        if (cond != null) {
            cond.show();
        }
        System.out.println("SEMICN ;");
        if (forStmtRight != null) {
            forStmtRight.show();
        }
        System.out.println("RPARENT )");
        stmt.show();
        super.show();
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        super.checkError(errorCollector);
        if (forStmtLeft != null) {
            forStmtLeft.checkError(errorCollector);
        }
        if (cond != null) {
            cond.checkError(errorCollector);
        }
        if (forStmtRight != null) {
            forStmtRight.checkError(errorCollector);
        }
        ErrUseSymbolManager.SM.enterLoop();
        stmt.checkError(errorCollector);
        ErrUseSymbolManager.SM.exitLoop();
    }

    @Override
    public Value getIRCode() {
        if (forStmtLeft != null) {
            forStmtLeft.getIRCode();
        }
        BasicBlock condBlock = new BasicBlock();
        // 这个是为了跳回来准备的
        BasicBlock loopBlock = new BasicBlock();
        // 专门为了循环体开的
        BasicBlock loopEndInstrBlock = new BasicBlock();
        // 这个是每次循环回来操作的指令
        BasicBlock afterLoopBlock = new BasicBlock();
        // 这个是循环后面的指令的block
        JumpInstr jumpFrontToCond = new JumpInstr(condBlock);
        // 由此指令前跳到cond
        IRBuilder.IB.addInstrForBlock(jumpFrontToCond);
        Loop loop = new Loop(
                condBlock,
                loopBlock,
                loopEndInstrBlock,
                afterLoopBlock
        );
        // loop结构
        IRBuilder.IB.enterLoop(loop);
        // 进入循环体，可以break 和 continue
        IRBuilder.IB.enterBlock(condBlock);
        // 进入到condBlock
        if (cond != null) {
            cond.genCondIr(loopBlock, afterLoopBlock);
        }
        // 如果成立的话需要跳进入循环体，否则跳出彻底，显然cond本身满足了这样的性质，因此不用专门跳了
        IRBuilder.IB.enterBlock(loopBlock);
        //  进入循环体
        stmt.getIRCode();
        // 解析循环体内容
        JumpInstr jumpToEndInstr = new JumpInstr(loopEndInstrBlock);
        // 在最后必须要回到end stuff
        IRBuilder.IB.addInstrForBlock(jumpToEndInstr);
        IRBuilder.IB.enterBlock(loopEndInstrBlock);
        // 开始设计end 的内容
        if (forStmtRight != null) {
            forStmtRight.getIRCode();
        }
        // 如果存在的话就生成，不过无论如何必须要这么一个块，因为还要跳回去。
        JumpInstr jumpBackToCond = new JumpInstr(condBlock);
        IRBuilder.IB.addInstrForBlock(jumpBackToCond);
        IRBuilder.IB.enterBlock(afterLoopBlock);
        IRBuilder.IB.exitLoop();
        return null;
    }
}
