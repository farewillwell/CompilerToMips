package front_end.ErrUseSymbols;

import front_end.AST.Exp.UnaryExp;
import front_end.AST.Fun.FuncDef;
import front_end.AST.Fun.FuncFParam;
import front_end.AST.Fun.FuncRParams;
import front_end.AST.TokenNode;
import front_end.AST.Var.ConstDef;
import front_end.AST.Var.VarDef;

import java.util.Stack;

public class ErrUseSymbolManager {
    public static final ErrUseSymbolManager SM = new ErrUseSymbolManager();

    public boolean isInVoidFunc = false;
    //当前所在的函数，如果现在在void所在的函数内部，那么所有的return int都要报错！！！
    //考虑到函数实际上只有一层

    private final Stack<ErrUseSymbolTable> errUseSymbolTables;   //每进到一个块就新建一个符号表，这个是变量的符号表
    private final ErrUseSymbolTable funcTable;

    private int loopDepth; // for check continue and break


    public void enterLoop() {
        loopDepth++;
    }

    public void exitLoop() {
        loopDepth--;
    }

    public boolean notInLoop() {
        return loopDepth <= 0;
    }

    public ErrUseSymbolManager() {
        errUseSymbolTables = new Stack<>();
        errUseSymbolTables.push(new ErrUseSymbolTable());
        funcTable = new ErrUseSymbolTable();
        loopDepth = 0;
    }

    public void debugPrint() {
        for (int i = 0; i < errUseSymbolTables.size(); i++) {
            System.out.println("table " + i);
            errUseSymbolTables.get(i).debugPrint();
        }
    }

    public void newDeclareConst(ConstDef constDef) {
        int dim = constDef.getDim();
        VarErrUseSymbol varSymbol;
        if (dim == 0) {
            varSymbol = new VarZeroErrUseSymbol(constDef);
        } else if (dim == 1) {
            varSymbol = new VarOneErrUseSymbol(constDef);
        } else {
            varSymbol = new ValTwoErrUseSymbol(constDef);
        }
        errUseSymbolTables.peek().addSymbol(varSymbol);
    }

    public void newDeclareVar(VarDef varDef) {
        int dim = varDef.getDim();
        VarErrUseSymbol varSymbol;
        if (dim == 0) {
            varSymbol = new VarZeroErrUseSymbol(varDef);
        } else if (dim == 1) {
            varSymbol = new VarOneErrUseSymbol(varDef);
        } else {
            varSymbol = new ValTwoErrUseSymbol(varDef);
        }
        errUseSymbolTables.peek().addSymbol(varSymbol);
    }

    public void newDeclarePara(FuncFParam funcFParam) {
        errUseSymbolTables.peek().addSymbol(new ErrUseParaErrUseSymbol(funcFParam));
    }

    public void newDeclareFunc(FuncDef funcDef) {
        funcTable.addSymbol(new ErrUseFuncErrUseSymbol(funcDef));
    }

    public void enterNewBlock() {
        errUseSymbolTables.push(new ErrUseSymbolTable());
    }

    public void exitBlock() {
        errUseSymbolTables.pop();
    }
    // 在不同作用域，函数和变量名字可以相同
    // 也就是说只要不是全局，那么函数和变量确实可以相同名
    // 可以相同是认识到的，全局的不相同是没意识到的.
    // 不可能去大改了，因此采取补丁，就是全局状态下额外需要检查一下有无同名函数或者同名变量

    public boolean isGlobal() {
        return errUseSymbolTables.size() == 1;
    }

    public boolean blockHasDefineVar(String name) {
        return errUseSymbolTables.peek().hasDefine(name);
    }

    public boolean hasDefineFunc(String name) {
        return funcTable.hasDefine(name);
    }

    public boolean notDefineVarYet(String name) {
        for (int i = errUseSymbolTables.size() - 1; i >= 0; i--) {
            if (errUseSymbolTables.get(i).hasDefine(name)) {
                return false;
            }
        }
        return true;
    }

    public boolean notDefineFuncYet(String name) {
        return !funcTable.hasDefine(name);
    }

    public VarErrUseSymbol getVarSymbol(String name) {
        for (int i = errUseSymbolTables.size() - 1; i >= 0; i--) {
            if (errUseSymbolTables.get(i).hasDefine(name)) {
                return (VarErrUseSymbol) errUseSymbolTables.get(i).getSymbol(name);
            }
        }
        SM.debugPrint();
        //throw new RuntimeException("no such symbol " + name);
        return null;
    }

    public ErrUseFuncErrUseSymbol getFuncSymbol(String name) {
        return (ErrUseFuncErrUseSymbol) funcTable.getSymbol(name);
    }


    /*TODO 假如是未定义则返回false, 为了赋值语句错误而设计*/
    public boolean identIsVarButConst(TokenNode tokenNode) {
        String string = tokenNode.content();
        VarErrUseSymbol symbol = getVarSymbol(string);
        if (symbol == null) {
            return false;
        }
        return symbol.isConst;
    }

    public boolean paraNumOk(String name, int paraNum) {
        if (notDefineFuncYet(name)) {
            //throw new RuntimeException("not define the func need to output");
            return true; // TODO 默认不检查未定义函数的行为
        }
        ErrUseFuncErrUseSymbol symbol = getFuncSymbol(name);
        return paraNum == symbol.paraNum;
    }

    // type check
    /*TODO  0~2 ok
     *  3 void
     */

    /*sec check
     * 0~m ok
     * -1: undefine */

    public boolean rightParasType(UnaryExp unaryExp) {
        ErrUseFuncErrUseSymbol errUseFuncSymbol = getFuncSymbol(unaryExp.tokenNode.content());
        FuncRParams funcRParams = unaryExp.funcRParams;
        if (funcRParams == null && errUseFuncSymbol.paraNum == 0) {
            return true;
        }
        assert funcRParams != null;
        int dim;
        try {
            dim = funcRParams.exp.getDim();
        } catch (RuntimeException e) {
            return true;
        }
        if (dim != errUseFuncSymbol.paraDim.get(0)) {
            //System.out.println(dim+" "+ errUseFuncSymbol.paraDim.get(0));
            return false;
        }
        if (dim == 2 && errUseFuncSymbol.paraSecondLength.get(0) != funcRParams.exp.getSec()) {
            //System.out.println(funcRParams.exp.getSec()+" "+errUseFuncSymbol.paraSecondLength.get(0));
            return false;
        }

        for (int i = 1; i < errUseFuncSymbol.paraDim.size(); i++) {
            int theDim;
            try {
                theDim = funcRParams.otherExps.get(i - 1).getDim();
            } catch (RuntimeException e) {
                return true;
            }
            if (theDim != errUseFuncSymbol.paraDim.get(i)) {
                //System.out.println(3);
                return false;
            }
            // 之前这里有bug，仅仅测试了第一个参数
            if (theDim == 2 && errUseFuncSymbol.paraSecondLength.get(i)
                    != funcRParams.otherExps.get(i - 1).getSec()) {
                //System.out.println(4);
                return false;
            }
        }
        return true;
    }
}
