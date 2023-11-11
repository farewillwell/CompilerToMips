package front_end.AST.Exp;

import front_end.AST.Node;
import front_end.ErrUseSymbols.ErrUseSymbolManager;
import front_end.ErrorCollector;

public class ForStmt extends Node {
    private final LVal lVal;
    private final Exp exp;

    public ForStmt(LVal lVal, Exp exp) {
        this.lVal = lVal;
        this.exp = exp;
    }

    @Override
    public void show() {
        lVal.show();
        System.out.println("ASSIGN =");
        exp.show();
        System.out.println("<ForStmt>");
        super.show();
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        super.checkError(errorCollector);
        if (ErrUseSymbolManager.SM.identIsVarButConst(lVal.getTokenNode())) {
            errorCollector.addError(lVal.getTokenNode().getLine(), "h");
        }
        lVal.checkError(errorCollector);
        exp.checkError(errorCollector);
    }
}
