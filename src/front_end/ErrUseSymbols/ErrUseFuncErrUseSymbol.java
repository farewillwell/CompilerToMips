package front_end.ErrUseSymbols;

import front_end.AST.Fun.FuncDef;
import front_end.AST.Fun.FuncFParam;
import front_end.AST.Fun.FuncFParams;
import front_end.RW;

import java.util.ArrayList;

public class ErrUseFuncErrUseSymbol extends ErrUseSymbol {
    public boolean returnIsVoid; // true : void ,false :int
    public int paraNum;
    public final ArrayList<Integer> paraDim; // 0 for a, 1 for a[] 2, for a[][m]

    public final ArrayList<Integer> paraSecondLength; // 0 for a/a[] , m for a[][m]

    public ErrUseFuncErrUseSymbol(FuncDef funcDef) {
        this.name = funcDef.tokenNode.content();
        FuncFParams funcFParams = funcDef.funcFParams;
        paraDim = new ArrayList<>();
        paraSecondLength = new ArrayList<>();
        this.returnIsVoid = funcDef.funcType.getType() == RW.TYPE.VOIDTK;
        if (funcFParams == null) {
            this.paraNum = 0;
        } else {
            this.paraNum = funcFParams.getParaNum();
            paraDim.add(funcFParams.funcFParam.getDim());
            if (funcFParams.funcFParam.getDim() == 2) {
                paraSecondLength.add(funcFParams.funcFParam.getSecondLength());
            } else {
                paraSecondLength.add(0);
            }
            for (int i = 1; i < paraNum; i++) {
                FuncFParam funcFParam = funcFParams.otherFuncFParams.get(i - 1);
                paraDim.add(funcFParam.getDim());
                if (funcFParam.getDim() == 2) {
                    paraSecondLength.add(funcFParam.getSecondLength());
                } else {
                    paraSecondLength.add(0);
                }
            }
        }
    }
}
