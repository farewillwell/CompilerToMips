package front_end.AST.Stmt;

import front_end.AST.Exp.LVal;
import front_end.ErrUseSymbols.ErrUseSymbolManager;
import front_end.ErrorCollector;
import mid_end.llvm_ir.IRBuilder;
import mid_end.llvm_ir.Instrs.IO.GetInt;
import mid_end.llvm_ir.Instrs.StoreInstr;
import mid_end.llvm_ir.Value;
import mid_end.symbols.SymbolManager;

public class GetIntStmt extends AssignStmt {

    public GetIntStmt(LVal lVal) {
        super(lVal, null);
    }

    @Override
    public void show() {
        lVal.show();
        System.out.println("ASSIGN =\n" +
                "GETINTTK getint\n" +
                "LPARENT (\n" +
                "RPARENT )\n" +
                "SEMICN ;");
        super.show();
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        if (!ErrUseSymbolManager.SM.notDefineVarYet(lVal.getTokenNode().content())) {
            if (ErrUseSymbolManager.SM.identIsVarButConst(lVal.getTokenNode())) {
                errorCollector.addError(lVal.getTokenNode().getLine(), "h");
            }
        }
        lVal.checkError(errorCollector);
    }

    @Override
    public Value getIRCode() {
        GetInt getInt = new GetInt();
        IRBuilder.IB.addInstrForBlock(getInt);
        storeIntoWith(getInt.getAns());
        return null;
    }
}
