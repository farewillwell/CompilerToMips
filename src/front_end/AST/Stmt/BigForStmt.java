package front_end.AST.Stmt;

import front_end.AST.Exp.Cond;
import front_end.ErrUseSymbols.ErrUseSymbolManager;
import front_end.ErrorCollector;

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
}
