package front_end.AST.Stmt;

import front_end.AST.Exp.Exp;
import front_end.ErrUseSymbols.ErrUseSymbolManager;
import front_end.ErrorCollector;
import mid_end.llvm_ir.IRBuilder;
import mid_end.llvm_ir.Instrs.ReturnInstr;
import mid_end.llvm_ir.User;
import mid_end.llvm_ir.Value;

public class ReturnStmt extends Stmt {
    private final Exp exp;
    private final int returnLine;

    public ReturnStmt(int returnLine) {
        this.exp = null;
        this.returnLine = returnLine;
    }


    public int getReturnLine() {
        return returnLine;
    }

    public ReturnStmt(Exp exp, int returnLine) {
        this.exp = exp;
        this.returnLine = returnLine;
    }

    @Override
    public void show() {
        System.out.println("RETURNTK return");
        if (exp != null) {
            exp.show();
        }
        System.out.println("SEMICN ;");
        super.show();
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        super.checkError(errorCollector);
        if (exp != null && ErrUseSymbolManager.SM.isInVoidFunc) {
            errorCollector.addError(returnLine, "f");
        }
    }

    @Override
    public Value getIRCode() {
        if (exp != null) {
            Value value = exp.getIRCode();
            IRBuilder.IB.addInstrForBlock(new ReturnInstr(value));
        } else {
            IRBuilder.IB.addInstrForBlock(new ReturnInstr());
        }
        return null;
    }
}
