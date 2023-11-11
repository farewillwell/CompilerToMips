package front_end.ErrUseSymbols;

import front_end.AST.Var.ConstDef;
import front_end.AST.Var.VarDef;

public class VarZeroErrUseSymbol extends VarErrUseSymbol {
    public int value;

    public VarZeroErrUseSymbol(ConstDef constDef) {
        this.isConst = true;
        this.name = constDef.getName();
        this.value = constDef.constInitVal.getValue();
    }

    public VarZeroErrUseSymbol(VarDef varDef) {
        this.isConst = false;
        this.name = varDef.getName();
        value = 0;
    }

    public int getValue() {
        return value;
    }
}
