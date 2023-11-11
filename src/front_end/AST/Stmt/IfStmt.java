package front_end.AST.Stmt;

import front_end.AST.Exp.Cond;
import front_end.ErrorCollector;

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
}
