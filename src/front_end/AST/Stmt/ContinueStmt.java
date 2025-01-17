package front_end.AST.Stmt;

import front_end.ErrUseSymbols.ErrUseSymbolManager;
import front_end.ErrorCollector;
import mid_end.llvm_ir.IRBuilder;
import mid_end.llvm_ir.Instrs.JumpIr;
import mid_end.llvm_ir.Loop;
import mid_end.llvm_ir.Value;

public class ContinueStmt extends Stmt{
    private final int continueLine;

    public ContinueStmt(int continueLine) {
        this.continueLine = continueLine;
    }

    @Override
    public void show() {
        System.out.println("CONTINUETK continue");
        System.out.println("SEMICN ;");
        super.show();
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        super.checkError(errorCollector);
        if (ErrUseSymbolManager.SM.notInLoop()) {
            errorCollector.addError(continueLine, "m");
        }
    }

    @Override
    public Value getIRCode() {
        Loop loop = IRBuilder.IB.getCurLoop();
        JumpIr jumpIr = new JumpIr(loop.loopEndInstrBlock);
        IRBuilder.IB.addInstrForBlock(jumpIr);
        return null;
    }
}
