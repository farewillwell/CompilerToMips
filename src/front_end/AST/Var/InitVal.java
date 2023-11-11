package front_end.AST.Var;

import front_end.AST.Exp.Exp;
import front_end.AST.Node;
import front_end.ErrorCollector;
import mid_end.llvm_ir.Initial;
import mid_end.llvm_ir.Value;
import mid_end.llvm_ir.type.ArrayType;
import mid_end.symbols.SymbolManager;

import java.util.ArrayList;

public class InitVal extends Node {
    private final boolean isBraces;

    private Exp exp;

    private InitVal initVal;
    private final ArrayList<InitVal> otherInitVals;

    public InitVal(Exp exp) {
        this.isBraces = false;
        this.exp = exp;
        initVal = null;
        otherInitVals = new ArrayList<>();
    }

    public InitVal() {
        this.isBraces = true;
        this.exp = null;
        initVal = null;
        otherInitVals = new ArrayList<>();
    }


    public void setExp(Exp exp) {
        this.exp = exp;
    }

    public void setInitVal(InitVal initVal) {
        this.initVal = initVal;
    }

    public void addOtherInitVals(InitVal initVal) {
        otherInitVals.add(initVal);
    }



    @Override
    public void show() {
        super.show();
        if (isBraces) {
            System.out.println("LBRACE {");
            if (initVal != null) {
                initVal.show();
                for (InitVal initVal1 : otherInitVals) {
                    System.out.println("COMMA ,");
                    initVal1.show();
                }
            }
            System.out.println("RBRACE }");
        } else {
            exp.show();
        }
        System.out.println("<InitVal>");
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        if (exp != null) {
            exp.checkError(errorCollector);
        }
        if (initVal != null) {
            initVal.checkError(errorCollector);
        }
        for (InitVal initVal1 : otherInitVals) {
            initVal1.checkError(errorCollector);
        }
    }

    @Override
    public Value getIRCode() {
        if (SymbolManager.SM.isGlobal()) {
            if (!isBraces) {
                return new Initial(exp.queryValue());
            } else {
                ArrayList<Initial> initials = new ArrayList<>();
                initials.add((Initial) initVal.getIRCode());
                for (InitVal initVal1 : otherInitVals) {
                    initials.add((Initial) initVal1.getIRCode());
                }
                return new Initial(new ArrayType(initials.size(), initials.get(0).containType), initials);
            }
        } else {
            return exp.getIRCode();
            // 这里默认只会赋值给非数组**********************************************
        }
    }
}
