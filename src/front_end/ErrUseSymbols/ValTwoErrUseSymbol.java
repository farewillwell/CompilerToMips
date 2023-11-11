package front_end.ErrUseSymbols;

import front_end.AST.Var.ConstDef;
import front_end.AST.Var.VarDef;

import java.util.ArrayList;

public class ValTwoErrUseSymbol extends VarErrUseSymbol {

    private final int firstDimension;

    public int secondDimension;

    public ArrayList<ArrayList<Integer>> arrayList;

    public int getValue(int para1, int para2) {
        return arrayList.get(para1).get(para2);
    }

    public ValTwoErrUseSymbol(VarDef varDef) {
        isConst = false;
        firstDimension = varDef.constExps.get(0).evaluate();
        secondDimension = varDef.constExps.get(1).evaluate();
        this.name = varDef.getName();
        arrayList = new ArrayList<>();
        for (int i = 0; i < firstDimension; i++) {
            arrayList.add(new ArrayList<>());
            for (int j = 0; j < secondDimension; j++) {
                arrayList.get(i).add(0);
            }
        }
    }

    public ValTwoErrUseSymbol(ConstDef constDef) {
        isConst = true;
        firstDimension = constDef.constExps.get(0).evaluate();
        secondDimension = constDef.constExps.get(1).evaluate();
        this.name = constDef.getName();
        arrayList = new ArrayList<>();
        for (int i = 0; i < firstDimension; i++) {
            arrayList.add(new ArrayList<>());
            for (int j = 0; j < secondDimension; j++) {
                arrayList.get(i).add(constDef.constInitVal.getValue(i, j));
            }
        }

    }
}
