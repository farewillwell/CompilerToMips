package front_end.AST.Exp;

import front_end.AST.Node;
import front_end.AST.TokenNode;
import front_end.ErrUseSymbols.*;
import front_end.ErrorCollector;
import mid_end.llvm_ir.*;
import mid_end.llvm_ir.Instrs.GEPIr;
import mid_end.llvm_ir.Instrs.LoadIr;
import mid_end.llvm_ir.type.ArrayType;
import mid_end.llvm_ir.type.PointerType;
import mid_end.symbols.SymbolManager;
import mid_end.symbols.VarSymbol;

import java.util.ArrayList;

public class LVal extends Node {
    // TODO 默认左值是一个数，因此这个exps 必然和其维度对应
    private final TokenNode tokenNode;
    public final ArrayList<Exp> exps;

    public String getName() {
        return tokenNode.content();
    }

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
            return ((ErrUseParaErrUseSymbol) varSymbol).dim - exps.size();
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

    // 仅仅在全局初始化计算的时候才使用。否则无效
    // 默认该方法得到的不是数组，因此可以用exp(不能用getDim())判断维度


    // 文法规范中定义：常量表达式 ConstExp → AddExp 注：使用的Ident 必须是常量 // 存在即可
    // 要求是常量，没有要求在什么阶段整的
    // 常量不要求必须是全局变量，因为在文法中指出，必须在编译时求值到非负整数，显然我局部定义const a=10也是可以的.
    // ConstDef 中表示各维长度的 ConstExp 都必须能在编译时求值到非负整数
    @Override
    public int queryValue() {
        VarSymbol varSymbol = SymbolManager.SM.getVarSymbol(tokenNode.content());
        if (varSymbol.value instanceof GlobalVar) {
            GlobalVar value = (GlobalVar) varSymbol.value;
            if (exps.size() == 0) {
                return value.getInitValue();
            } else if (exps.size() == 1) {
                return value.getInitValue(exps.get(0).queryValue());
            }
            return value.getInitValue(exps.get(0).queryValue(), exps.get(1).queryValue());
        } else {
            LocalVar value = (LocalVar) varSymbol.value;
            if (exps.size() == 0) {
                return value.getInitValue();
            } else if (exps.size() == 1) {
                return value.getInitValue(exps.get(0).queryValue());
            }
            return value.getInitValue(exps.get(0).queryValue(), exps.get(1).queryValue());
        }
    }

    // TODO 默认取出来是整数  这个值方法返回的是这个lval对应的东西的地址，是作为值的，我们需要一个方法作为被赋值的东西
    // 不同维度的区别在于是使用直接符号表存的作为地址还是用中间变量作为地址。
    @Override
    public Value getIRCode() {
        // 这个是存放实际需要数值的指针
        Value nowArrayOrAnsPointer = SymbolManager.SM.getVarSymbol(tokenNode.content()).value;
        for (Exp exp : exps) {
            GEPIr gepIr = new GEPIr(nowArrayOrAnsPointer, exp.getIRCode());
            IRBuilder.IB.addInstrForBlock(gepIr);
            nowArrayOrAnsPointer = gepIr.getAns();
        }
        // 如果是0维，就是单个变量，那么就是一个简单的load取出来就好了，两个都一样。
        //TODO 假如得到的指针里面的内容还是一个数组呢？
        // 如果是int*的那就说明就是到数字了，要是[]*的说明是作为参数传进去的
        // 就是，如果获得的指针指向的是数组，那么就再做一轮gep，不管最后剩下啥。
        if (((PointerType) nowArrayOrAnsPointer.type).objType instanceof ArrayType) {
            GEPIr gepIr = new GEPIr(nowArrayOrAnsPointer, new Constant(0));
            IRBuilder.IB.addInstrForBlock(gepIr);
            return gepIr.getAns();
        }
        LoadIr loadIr = new LoadIr(nowArrayOrAnsPointer);
        IRBuilder.IB.addInstrForBlock(loadIr);
        return loadIr.getAns();
    }
}
