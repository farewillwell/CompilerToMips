package front_end.AST.Var;

import front_end.AST.Exp.ConstExp;
import front_end.AST.Node;
import front_end.ErrorCollector;
import mid_end.llvm_ir.Initial;
import mid_end.llvm_ir.Value;
import mid_end.llvm_ir.type.ArrayType;
import mid_end.symbols.SymbolManager;

import java.util.ArrayList;

public class ConstInitVal extends Node {
    private final boolean isBraces;

    private ConstExp constExp;

    public ConstInitVal constInitVal;
    public ArrayList<ConstInitVal> otherConstInitVals;

    public ConstInitVal(ConstExp constExp) {
        this.isBraces = false;
        this.constExp = constExp;
        constInitVal = null;
        otherConstInitVals = new ArrayList<>();
    }

    public ConstInitVal() {
        this.isBraces = true;
        constExp = null;
        constInitVal = null;
        otherConstInitVals = new ArrayList<>();
    }


    public void setConstInitVal(ConstInitVal constInitVal) {
        this.constInitVal = constInitVal;
    }

    public void addOtherConstInitVals(ConstInitVal constInitVal) {
        otherConstInitVals.add(constInitVal);
    }


    @Override
    public void show() {
        super.show();
        if (isBraces) {
            System.out.println("LBRACE {");
            if (constInitVal != null) {
                constInitVal.show();
                for (ConstInitVal constInitVal1 : otherConstInitVals) {
                    System.out.println("COMMA ,");
                    constInitVal1.show();
                }
            }
            System.out.println("RBRACE }");
        } else {
            constExp.show();
        }
        System.out.println("<ConstInitVal>");
    }

    public int getValue(int para1, int para2) {
        if (para1 == 0) {
            return constInitVal.getValue(para2);
        } else {
            return otherConstInitVals.get(para1 - 1).getValue(para2);
        }
    }

    public int getValue(int para) {
        if (para == 0) {
            return constInitVal.getValue();
        } else {
            return otherConstInitVals.get(para - 1).getValue();
        }
    }

    public int getValue() {
        return constExp.evaluate();
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        if (!isBraces) {
            constExp.checkError(errorCollector);
        }
        if (constInitVal != null) {
            constInitVal.checkError(errorCollector);
        }
        for (ConstInitVal constInitVal1 : otherConstInitVals) {
            constInitVal1.checkError(errorCollector);
        }

    }

    @Override
    public Value getIRCode() {
        if (SymbolManager.SM.isGlobal()) {
            if (!isBraces) {
                return new Initial(constExp.queryValue());
            } else {
                ArrayList<Initial> initials = new ArrayList<>();
                initials.add((Initial) constInitVal.getIRCode());
                for (ConstInitVal constInitVal1 : otherConstInitVals) {
                    initials.add((Initial) constInitVal1.getIRCode());
                }
                return new Initial(new ArrayType(initials.size(), initials.get(0).containType), initials);
            }
        } else {
            return constExp.getIRCode();
            // 这里默认只会赋值给非数组**********************************************
        }
    }

    public Initial getInit() {
        if (!isBraces) {
            return new Initial(constExp.queryValue());
        } else {
            ArrayList<Initial> initials = new ArrayList<>();
            initials.add(constInitVal.getInit()); // 这里也要换成init而不是irCode
            for (ConstInitVal constInitVal1 : otherConstInitVals) {
                initials.add(constInitVal1.getInit());
            }
            return new Initial(new ArrayType(initials.size(), initials.get(0).containType), initials);
        }
    }

    public Value getIrByIndex(int x) {
        if (SymbolManager.SM.isGlobal()) {
            throw new RuntimeException(" ");
        }
        if (x == 0) {
            return constInitVal.getIRCode();
        } else {
            return otherConstInitVals.get(x - 1).getIRCode();
        }
    }

    public Value getIrByIndex(int x, int y) {
        if (SymbolManager.SM.isGlobal()) {
            throw new RuntimeException("");
        }
        if (x == 0) {
            if (y == 0) {
                return constInitVal.constInitVal.getIRCode();
            } else {
                return constInitVal.otherConstInitVals.get(y - 1).getIRCode();
            }
        } else {
            if (y == 0) {
                return otherConstInitVals.get(x - 1).constInitVal.getIRCode();
            } else {
                return otherConstInitVals.get(x - 1).otherConstInitVals.get(y - 1).getIRCode();
            }
        }
    }
}
