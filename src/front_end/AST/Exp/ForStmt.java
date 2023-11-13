package front_end.AST.Exp;

import front_end.AST.Stmt.AssignStmt;

public class ForStmt extends AssignStmt {
    // 都是赋值，可以直接继承

    public ForStmt(LVal lVal, Exp exp) {
        super(lVal,exp);
    }

    @Override
    public void show() {
        lVal.show();
        System.out.println("ASSIGN =");
        exp.show();
        System.out.println("<ForStmt>");
        super.show();
    }
    // check Err  和 lval 一样的
}
