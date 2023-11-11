package front_end.AST.Exp;

import front_end.AST.Node;
import front_end.AST.TokenNode;
import front_end.ErrUseSymbols.*;
import front_end.ErrorCollector;
import mid_end.llvm_ir.GlobalVar;
import mid_end.llvm_ir.IRBuilder;
import mid_end.llvm_ir.Instrs.LoadInstr;
import mid_end.llvm_ir.Value;
import mid_end.llvm_ir.type.BaseType;
import mid_end.symbols.SymbolManager;
import mid_end.symbols.VarSymbol;

import java.util.ArrayList;

public class LVal extends Node {
    private final TokenNode tokenNode;
    private final ArrayList<Exp> exps;

    public LVal(TokenNode tokenNode) {
        this.tokenNode = tokenNode;
        this.exps = new ArrayList<>();
    }

    public TokenNode getTokenNode() {
        return tokenNode;
    }

    public void addExp(Exp exp) {
        exps.add(exp);
    }

    @Override
    public void show() {
        tokenNode.show();
        for (Exp exp : exps) {
            System.out.println("LBRACK [");
            exp.show();
            System.out.println("RBRACK ]");
        }
        super.show();
        System.out.println("<LVal>");
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        super.checkError(errorCollector);
        if (ErrUseSymbolManager.SM.notDefineVarYet(tokenNode.content())) {
            errorCollector.addError(tokenNode.getLine(), "c");
        } else {
            for (Exp exp : exps) {
                exp.checkError(errorCollector);
            }
        }
    }
    /*TODO  THE EVALUATE OF LVAL AND THE GETDIM OF LVAL AND GETSEC OF THE LVAL*/

    public int evaluate() {
        String name = tokenNode.content();
        ErrUseSymbol errUseSymbol = ErrUseSymbolManager.SM.getVarSymbol(name);
        boolean flag = errUseSymbol instanceof VarZeroErrUseSymbol && exps.size() == 0;
        flag = flag || errUseSymbol instanceof VarOneErrUseSymbol && exps.size() == 1;
        flag = flag || errUseSymbol instanceof ValTwoErrUseSymbol && exps.size() == 2;
        if (!flag) {
            throw new RuntimeException("lvalue dim not match");
        }
        if (errUseSymbol instanceof VarZeroErrUseSymbol) {
            return ((VarZeroErrUseSymbol) errUseSymbol).getValue();
        } else if (errUseSymbol instanceof VarOneErrUseSymbol) {
            return ((VarOneErrUseSymbol) errUseSymbol).getValue(exps.get(0).evaluate());
        } else {
            return ((ValTwoErrUseSymbol) errUseSymbol).getValue(exps.get(0).evaluate(), exps.get(1).evaluate());
        }
    }

    /* TODO 后面新引进了一个叫做parameter的标记，但是实际上para在这里也算作未定义了*/
    public int getDim() {
        if (ErrUseSymbolManager.SM.notDefineVarYet(tokenNode.content())) {
            throw new RuntimeException("undefine");
        }
        VarErrUseSymbol varSymbol = ErrUseSymbolManager.SM.getVarSymbol(tokenNode.content());
        if (varSymbol instanceof ErrUseParaErrUseSymbol) {
            return ((ErrUseParaErrUseSymbol) varSymbol).dim-exps.size();
        }
        int dim;
        if (varSymbol instanceof VarZeroErrUseSymbol) {
            dim = 0;
        } else if (varSymbol instanceof VarOneErrUseSymbol) {
            dim = 1;
        } else {
            dim = 2;
        }
        dim -= exps.size();
        return dim;
    }

    public int getSec() {
        if (ErrUseSymbolManager.SM.notDefineVarYet(tokenNode.content())) {
            return 0;
        }
        VarErrUseSymbol varSymbol = ErrUseSymbolManager.SM.getVarSymbol(tokenNode.content());
        if (varSymbol instanceof ErrUseParaErrUseSymbol) {
            return ((ErrUseParaErrUseSymbol) varSymbol).secondLength;
        } else {
            if (!(varSymbol instanceof ValTwoErrUseSymbol)) {
                return 0;
            } else {
                return ((ValTwoErrUseSymbol) varSymbol).secondDimension;
            }
        }
    }

    // 默认取出来是整数 TODO 这个值方法返回的是作为值的，我们需要一个方法作为被赋值的东西
    @Override
    public Value getIRCode() {
        LoadInstr loadInstr = new LoadInstr(tokenNode.content(), BaseType.I32);
        IRBuilder.IB.addInstrForBlock(loadInstr);
        return loadInstr.getAns();
    }

    // TODO 这个方法是上面说的需要作为被赋值的码
    public Value getIRAsLeft() {
        return SymbolManager.SM.getVarSymbol(tokenNode.content()).value;
    }

    // 仅仅在全局初始化计算的时候才使用。否则无效
    @Override
    public int queryValue() {
        if (!SymbolManager.SM.isGlobal()) {
            throw new RuntimeException("evaluate in not global state");
        } else {
            VarSymbol varSymbol = SymbolManager.SM.getVarSymbol(tokenNode.content());
            if (!(varSymbol.value instanceof GlobalVar)) {
                throw new RuntimeException("evaluate from not global value");
            }
            return ((GlobalVar) varSymbol.value).getInitValue();
        }
    }
}
