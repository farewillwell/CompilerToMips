package front_end.AST.Stmt;

import front_end.AST.Exp.LVal;
import front_end.ErrUseSymbols.ErrUseSymbolManager;
import front_end.ErrorCollector;
import mid_end.llvm_ir.IRBuilder;
import mid_end.llvm_ir.Instrs.IO.GetInt;
import mid_end.llvm_ir.Instrs.StoreInstr;
import mid_end.llvm_ir.Value;

public class GetIntStmt extends Stmt {
    private final LVal lVal;

    public GetIntStmt(LVal lVal) {
        this.lVal = lVal;
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
        super.checkError(errorCollector);
        if (ErrUseSymbolManager.SM.identIsVarButConst(lVal.getTokenNode())) {
            errorCollector.addError(lVal.getTokenNode().getLine(), "h");
        }
    }

    @Override
    public Value getIRCode() {
        GetInt getInt = new GetInt();
        IRBuilder.IB.addInstrForBlock(getInt);
        StoreInstr storeInstr = new StoreInstr(getInt.getAns(), lVal.getIRAsLeft());
        IRBuilder.IB.addInstrForBlock(storeInstr);
        return null;
    }
}
