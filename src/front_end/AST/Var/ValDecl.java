package front_end.AST.Var;

import front_end.AST.Node;
import front_end.ErrorCollector;
import mid_end.llvm_ir.Value;

import java.util.ArrayList;

public class ValDecl extends Node {
    private final VarDef firstVarDef;
    private final ArrayList<VarDef> otherVarDefs;

    public ValDecl(VarDef varDef) {
        firstVarDef = varDef;
        otherVarDefs = new ArrayList<>();
    }

    public void addOtherVarDefs(VarDef varDef) {
        otherVarDefs.add(varDef);
    }

    @Override
    public void show() {
        super.show();
        System.out.println("INTTK int");
        firstVarDef.show();
        for (VarDef varDef : otherVarDefs) {
            System.out.println("COMMA ,");
            varDef.show();
        }
        System.out.println("SEMICN ;");
        System.out.println("<VarDecl>");
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        super.checkError(errorCollector);
        firstVarDef.checkError(errorCollector);
        for (VarDef varDef : otherVarDefs) {
            varDef.checkError(errorCollector);
        }
    }

    @Override
    public Value getIRCode() {
        firstVarDef.getIRCode();
        for (VarDef varDef : otherVarDefs) {
            varDef.getIRCode();
        }
        return null;
    }
}
