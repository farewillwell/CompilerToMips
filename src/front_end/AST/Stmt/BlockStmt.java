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
        // 这里就不能再新建块了，实际上，基本块和{}没关系，块是和符号表有关，基本块和跳转有关
        SymbolManager.SM.enterBlock();
        block.getIRCode();
        SymbolManager.SM.exitBlock();
        return null;
    }
}
