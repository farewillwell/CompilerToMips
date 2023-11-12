package front_end.AST.Stmt;

import front_end.AST.Exp.Exp;
import front_end.AST.Exp.LVal;
import front_end.ErrUseSymbols.ErrUseSymbolManager;
import front_end.ErrorCollector;
import mid_end.llvm_ir.GlobalVar;
import mid_end.llvm_ir.IRBuilder;
import mid_end.llvm_ir.Instrs.GEPInstr;
import mid_end.llvm_ir.Instrs.StoreInstr;
import mid_end.llvm_ir.LocalVar;
import mid_end.llvm_ir.Value;
import mid_end.symbols.SymbolManager;

import java.util.ArrayList;


public class AssignStmt extends Stmt {
    protected final LVal lVal;
    protected final Exp exp;

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
        Value valueToBeStore = exp.getIRCode();
        storeIntoWith(valueToBeStore);
        return null;
    }

    public void storeIntoWith(Value valueToBeStore) {
        Value toBeAssign = SymbolManager.SM.getVarSymbol(lVal.getName()).value;
        if (lVal.exps.size() == 0) {
            StoreInstr storeInstr = new StoreInstr(valueToBeStore, toBeAssign);
            IRBuilder.IB.addInstrForBlock(storeInstr);
        }
        /*
        if (toBeAssign instanceof GlobalVar) {
            ArrayList<Value> offsets = new ArrayList<>();
            for (Exp exp1 : lVal.exps) {
                offsets.add(exp1.getIRCode());
            }
            StoreInstr storeInstr = new StoreInstr(valueToBeStore, offsets, (GlobalVar) toBeAssign);
            IRBuilder.IB.addInstrForBlock(storeInstr);
        } else {*/
        LocalVar nowArrayOrAnsPointer = (LocalVar) toBeAssign;
        for (Exp exp1 : lVal.exps) {
            GEPInstr gepInstr = new GEPInstr(nowArrayOrAnsPointer, exp1.getIRCode());
            IRBuilder.IB.addInstrForBlock(gepInstr);
            nowArrayOrAnsPointer = (LocalVar) gepInstr.getAns();
        }
        StoreInstr storeInstr = new StoreInstr(valueToBeStore, nowArrayOrAnsPointer);
        IRBuilder.IB.addInstrForBlock(storeInstr);
        // }
    }
}
