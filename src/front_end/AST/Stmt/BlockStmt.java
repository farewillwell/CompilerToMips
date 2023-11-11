package front_end.AST.Stmt;

import front_end.AST.Block;
import front_end.ErrUseSymbols.ErrUseSymbolManager;
import front_end.ErrorCollector;
import mid_end.llvm_ir.BasicBlock;
import mid_end.llvm_ir.IRBuilder;
import mid_end.llvm_ir.Value;
import mid_end.symbols.SymbolManager;

public class BlockStmt extends Stmt {
    private final Block block;

    public BlockStmt(Block block) {
        this.block = block;
    }

    @Override
    public void show() {
        block.show();
        super.show();
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        ErrUseSymbolManager.SM.enterNewBlock();
        block.checkError(errorCollector);
        ErrUseSymbolManager.SM.exitBlock();
    }

    @Override
    public Value getIRCode() {
        BasicBlock basicBlock = new BasicBlock();
        IRBuilder.IB.enterBlock(basicBlock);
        block.getIRCode();
        SymbolManager.SM.enterBlock();
        SymbolManager.SM.exitBlock();
        return null;
    }
}
