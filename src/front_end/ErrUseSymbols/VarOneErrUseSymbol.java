package front_end.ErrUseSymbols;

import front_end.AST.Var.ConstDef;
import front_end.AST.Var.VarDef;

import java.util.ArrayList;

public class VarOneErrUseSymbol extends VarErrUseSymbol {
    public final int length;
    public final ArrayList<Integer> arrayList;

    public int getValue(int index) {
        return arrayList.get(index);
    }

    public VarOneErrUseSymbol(VarDef varDef) {
        isConst = false;
        arrayList = new ArrayList<>();
        this.name = varDef.getName();
        length = varDef.constExps.get(0).evaluate();
        for (int i = 0; i < length; i++) {
            arrayList.add(0);
        }
    }

    public VarOneErrUseSymbol(ConstDef constDef) {
        isConst = true;
        arrayList = new ArrayList<>();
        this.name = constDef.getName();
        length = constDef.constExps.get(0).evaluate();
        for (int i = 0; i < length; i++) {
            arrayList.add(constDef.constInitVal.getValue(i));
        }

    }

}
