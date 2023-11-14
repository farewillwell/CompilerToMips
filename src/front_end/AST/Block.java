package front_end.AST;

import front_end.AST.Fun.FuncFParams;
import front_end.AST.Stmt.ReturnStmt;
import front_end.ErrUseSymbols.ErrUseSymbolManager;
import front_end.ErrorCollector;
import mid_end.llvm_ir.BasicBlock;
import mid_end.llvm_ir.IRBuilder;
import mid_end.llvm_ir.Value;
import mid_end.symbols.SymbolManager;

import java.util.ArrayList;

public class Block extends Node {
    private final ArrayList<Node> nodes;

    public int getRBraceLine() {
        return RBraceLine;
    }

    private int RBraceLine;

    public Block() {
        nodes = new ArrayList<>();
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public void setRBraceLine(int line) {
        this.RBraceLine = line;
    }

    public boolean notHasReturnLastLine() {
        if (nodes.size() > 0) {
            return !(nodes.get(nodes.size() - 1) instanceof ReturnStmt);
        }
        return true;
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        ErrUseSymbolManager.SM.enterNewBlock();
        for (Node node : nodes) {
            node.checkError(errorCollector);
        }
        // 之前这里没退出去，居然没发现???
        ErrUseSymbolManager.SM.exitBlock();
    }

    public void checkErrAsFuncBody(ErrorCollector errorCollector, FuncFParams funcFParams)
    {
        ErrUseSymbolManager.SM.enterNewBlock();
        if (funcFParams != null) {
            funcFParams.checkError(errorCollector);
        }
        for (Node node : nodes) {
            node.checkError(errorCollector);
        }
        ErrUseSymbolManager.SM.exitBlock();
    }

    @Override
    public void show() {
        super.show();
        System.out.println("LBRACE {");
        for (Node node : nodes) {
            node.show();
        }
        System.out.println("RBRACE }");
        System.out.println("<Block>");
    }

    @Override
    public Value getIRCode() {
        for (Node node : nodes) {
            node.getIRCode();
        }
        return null;
    }
}
