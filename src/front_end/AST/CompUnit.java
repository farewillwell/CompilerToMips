package front_end.AST;

import front_end.AST.Fun.FuncDef;
import front_end.AST.Fun.MainFuncDef;
import front_end.ErrorCollector;
import mid_end.llvm_ir.*;

import java.util.ArrayList;

public class CompUnit extends Node {
    private final ArrayList<Node> decls;
    private final ArrayList<FuncDef> funcDefs;

    private MainFuncDef mainFuncDef;

    public CompUnit() {
        this.decls = new ArrayList<>();
        this.funcDefs = new ArrayList<>();
        mainFuncDef = null;
    }

    public void addDecl(Node node) {
        decls.add(node);
    }

    public void addFuncDef(FuncDef funcDef) {
        funcDefs.add(funcDef);
    }

    public void setMainFuncDef(MainFuncDef mainFuncDef) {
        this.mainFuncDef = mainFuncDef;
    }

    @Override
    public void show() {
        super.show();
        for (Node node : decls) {
            node.show();
        }
        for (FuncDef funcDef : funcDefs) {
            funcDef.show();
        }
        mainFuncDef.show();
        System.out.println("<CompUnit>");
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        super.checkError(errorCollector);
        for (Node node : decls) {
            node.checkError(errorCollector);
        }
        for (FuncDef funcDef : funcDefs) {
            funcDef.checkError(errorCollector);
        }
        mainFuncDef.checkError(errorCollector);
    }

    @Override
    public Value getIRCode() {
        for (Node node : decls) {
            node.getIRCode();
        }
        for (FuncDef funcDef : funcDefs) {
            IRBuilder.IB.moduleAddFunc((Function) funcDef.getIRCode());
        }
        IRBuilder.IB.moduleAddFunc((Function) mainFuncDef.getIRCode());
        return IRBuilder.IB.getModule();
    }
}
