package front_end.AST.Var;

import front_end.AST.Node;
import front_end.ErrorCollector;
import mid_end.llvm_ir.Value;

import java.util.ArrayList;

public class ConstDecl extends Node {
    private final ConstDef firstConstDef;
    private final ArrayList<ConstDef> otherConstDefs;

    public ConstDecl(ConstDef constDef) {
        firstConstDef = constDef;
        otherConstDefs = new ArrayList<>();
    }

    public void addConstDef(ConstDef constDef) {
        otherConstDefs.add(constDef);
    }

    @Override
    public void show() {
        super.show();
        System.out.println("CONSTTK const");
        System.out.println("INTTK int");
        firstConstDef.show();
        for (ConstDef constDef : otherConstDefs) {
            System.out.println("COMMA ,");
            constDef.show();
        }
        System.out.println("SEMICN ;");
        System.out.println("<ConstDecl>");
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        super.checkError(errorCollector);
        firstConstDef.checkError(errorCollector);
        for (ConstDef constDef : otherConstDefs) {
            constDef.checkError(errorCollector);
        }
    }

    @Override
    public Value getIRCode() {
        firstConstDef.getIRCode();
        for (ConstDef constDef : otherConstDefs) {
            constDef.getIRCode();
        }
        return null;
    }
}
