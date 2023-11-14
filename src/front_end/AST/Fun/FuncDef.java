package front_end.AST.Fun;

import front_end.AST.Block;
import front_end.AST.Node;
import front_end.AST.TokenNode;
import front_end.ErrUseSymbols.ErrUseSymbolManager;
import front_end.ErrorCollector;
import front_end.RW;
import mid_end.llvm_ir.*;
import mid_end.llvm_ir.type.BaseType;
import mid_end.symbols.FuncSymbol;
import mid_end.symbols.SymbolManager;

import java.util.ArrayList;

public class FuncDef extends Node {
    public final FuncType funcType;
    public final TokenNode tokenNode;
    public final FuncFParams funcFParams;
    public final Block block;

    public FuncDef(FuncType funcType, TokenNode tokenNode, FuncFParams funcFParams, Block block) {
        this.funcType = funcType;
        this.tokenNode = tokenNode;
        this.funcFParams = funcFParams;
        this.block = block;
    }

    public FuncDef(FuncType funcType, TokenNode tokenNode, Block block) {
        this.funcType = funcType;
        this.tokenNode = tokenNode;
        this.funcFParams = null;
        this.block = block;
    }

    @Override
    public void show() {
        super.show();
        funcType.show();
        tokenNode.show();
        System.out.println("LPARENT (");
        if (funcFParams != null) {
            funcFParams.show();
        }
        System.out.println("RPARENT )");
        block.show();
        System.out.println("<FuncDef>");
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        if (ErrUseSymbolManager.SM.hasDefineFunc(tokenNode.content())) {
            errorCollector.addError(tokenNode.getLine(), "b");
        } else if (ErrUseSymbolManager.SM.blockHasDefineVar(tokenNode.content())) {
            errorCollector.addError(tokenNode.getLine(), "b");
        } else {
            ErrUseSymbolManager.SM.newDeclareFunc(this);
            if (this.funcType.getType() == RW.TYPE.INTTK && this.block.notHasReturnLastLine()) {
                errorCollector.addError(block.getRBraceLine(), "g");
            }
            if (this.funcType.getType() == RW.TYPE.VOIDTK) {
                //errorCollector.addError(block.intReturnLine(), "f");
                ErrUseSymbolManager.SM.isInVoidFunc = true;
            }
            // 不能在这里enterNewBlock,因为实际上参数的block和函数的body是一起的
            // 会导致定义域错误
            block.checkErrAsFuncBody(errorCollector, funcFParams);
            ErrUseSymbolManager.SM.isInVoidFunc = false;
        }
    }

    @Override
    public Value getIRCode() {
        BaseType baseType = funcType.getType() == RW.TYPE.VOIDTK ? BaseType.Void : BaseType.I32;
        Function function = new Function(baseType, tokenNode.content());
        SymbolManager.SM.addFuncSymbol(new FuncSymbol(tokenNode.content(), function));
        IRBuilder.IB.enterFunc(function);
        BasicBlock basicBlock = new BasicBlock();
        IRBuilder.IB.enterBlock(basicBlock);
        SymbolManager.SM.enterBlock();
        if (funcFParams != null) {
            ArrayList<Param> paras = funcFParams.getParas();
            for (Param para : paras) {
                function.addPara(para);
            }
        }
        block.getIRCode();
        SymbolManager.SM.exitBlock();
        return function;
    }
}
