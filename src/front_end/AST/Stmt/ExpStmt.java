package front_end.AST.Stmt;

import front_end.AST.Exp.Exp;
import front_end.ErrorCollector;
import mid_end.llvm_ir.IRBuilder;
import mid_end.llvm_ir.Value;


public class ExpStmt extends Stmt {
    private final Exp exp;

    public ExpStmt(Exp exp) {
        this.exp = exp;
    }

    public ExpStmt() {
        this.exp = null;
    }

    @Override
    public void show() {
        if (exp != null) {
            exp.show();
        }
        System.out.println("SEMICN ;");
        super.show();
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        if (exp != null) {
            exp.checkError(errorCollector);
        }
    }

    @Override
    public Value getIRCode() {
        if (exp != null) {
            exp.getIRCode();
        }
        return null;
    }
}
