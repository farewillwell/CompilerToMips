package front_end.AST.Fun;

import front_end.AST.Block;
import front_end.AST.Node;
import front_end.ErrUseSymbols.ErrUseSymbolManager;
import front_end.ErrorCollector;
import mid_end.llvm_ir.BasicBlock;
import mid_end.llvm_ir.IRBuilder;
import mid_end.llvm_ir.MainFunction;
import mid_end.llvm_ir.Value;
import mid_end.llvm_ir.type.BaseType;
import mid_end.symbols.SymbolManager;

public class MainFuncDef extends Node {
    private final Block block;

    public MainFuncDef(Block block) {
        this.block = block;
    }

    @Override
    public void show() {
        super.show();
        System.out.println("INTTK int\n" +
                "MAINTK main\n" +
                "LPARENT (\n" +
                "RPARENT )");
        block.show();
        System.out.println("<MainFuncDef>");
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        if (this.block.notHasReturnLastLine()) {
            errorCollector.addError(block.getRBraceLine(), "g");
        }
        ErrUseSymbolManager.SM.enterNewBlock();
        block.checkError(errorCollector);
        ErrUseSymbolManager.SM.exitBlock();
    }

    @Override
    public Value getIRCode() {
        MainFunction mainFunction = new MainFunction();
        IRBuilder.IB.enterFunc(mainFunction);
        BasicBlock basicBlock = new BasicBlock();
        IRBuilder.IB.enterBlock(basicBlock);
        SymbolManager.SM.enterBlock();
        block.getIRCode();
        SymbolManager.SM.exitBlock();
        return mainFunction;
    }
}
