package front_end.AST.Stmt;

import front_end.AST.Exp.Exp;
import front_end.AST.Exp.LVal;
import front_end.ErrUseSymbols.ErrUseSymbolManager;
import front_end.ErrorCollector;
import mid_end.llvm_ir.IRBuilder;
import mid_end.llvm_ir.Instrs.StoreInstr;
import mid_end.llvm_ir.Value;


public class AssignStmt extends Stmt {
    private final LVal lVal;
    private final Exp exp;

    public AssignStmt(LVal lVal, Exp exp) {
        this.lVal = lVal;
        this.exp = exp;
    }

    @Override
    public void show() {
        lVal.show();
        System.out.println("ASSIGN =");
        exp.show();
        System.out.println("SEMICN ;");
        super.show();
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        super.checkError(errorCollector);
        if (!ErrUseSymbolManager.SM.notDefineVarYet(lVal.getTokenNode().content())) {
            if (ErrUseSymbolManager.SM.identIsVarButConst(lVal.getTokenNode())) {
                errorCollector.addError(lVal.getTokenNode().getLine(), "h");
            }
        }
        lVal.checkError(errorCollector);
        exp.checkError(errorCollector);
    }

    @Override
    public Value getIRCode() {
        Value value = exp.getIRCode();
        StoreInstr storeInstr = new StoreInstr(value, lVal.getIRAsLeft());
        IRBuilder.IB.addInstrForBlock(storeInstr);
        return null;
    }
}
