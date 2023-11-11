package front_end.ErrUseSymbols;

import front_end.AST.Fun.FuncFParam;

public class ErrUseParaErrUseSymbol extends VarErrUseSymbol {
    public int dim;
    public int secondLength ; // a[][secondLength]

    public ErrUseParaErrUseSymbol(FuncFParam funcFParam) {
        this.name = funcFParam.tokenNode.content();
        this.isConst = false;
        this.secondLength = 0;
        if (!funcFParam.hasBrack) {
            this.dim = 0;
        } else {
            if (funcFParam.constExps.size() == 0) {
                this.dim = 1;
            } else {
                this.dim = 2;
                this.secondLength = funcFParam.getSecondLength();
            }
        }
    }
}
