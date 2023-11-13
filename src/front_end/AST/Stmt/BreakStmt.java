package front_end.AST.Stmt;

import front_end.ErrUseSymbols.ErrUseSymbolManager;
import front_end.ErrorCollector;
import mid_end.llvm_ir.IRBuilder;
import mid_end.llvm_ir.Instrs.JumpInstr;
import mid_end.llvm_ir.Instrs.Loop;
import mid_end.llvm_ir.Value;

public class BreakStmt extends Stmt {
    private final int breakLine;

    public BreakStmt(int breakLine) {
        this.breakLine = breakLine;
    }

    @Override
    public void show() {
        System.out.println("BREAKTK break");
        System.out.println("SEMICN ;");
        super.show();
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        super.checkError(errorCollector);
        if (ErrUseSymbolManager.SM.notInLoop()) {
            errorCollector.addError(breakLine, "m");
        }
    }

    @Override
    public Value getIRCode() {
        Loop loop = IRBuilder.IB.getCurLoop();
        JumpInstr jumpInstr = new JumpInstr(loop.afterLoopBlock);
        IRBuilder.IB.addInstrForBlock(jumpInstr);
        return null;
    }
}
