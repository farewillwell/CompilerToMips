package front_end.AST.Stmt;

import front_end.ErrUseSymbols.ErrUseSymbolManager;
import front_end.ErrorCollector;

public class BreakStmt extends Stmt{
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
}
