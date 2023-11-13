package front_end.AST.Exp;

import front_end.AST.Node;
import front_end.ErrorCollector;
import mid_end.llvm_ir.Value;

public class ConstExp extends Node {
    private final AddExp addExp;

    public ConstExp(AddExp addExp) {
        this.addExp = addExp;
    }

    @Override
    public void show() {
        addExp.show();
        System.out.println("<ConstExp>");
        super.show();
    }

    public int evaluate() {
        return addExp.evaluate();
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        addExp.checkError(errorCollector);
    }

    public int getDim() {
        return addExp.getDim();
    }

    @Override
    public int queryValue() {
        return addExp.queryValue();
    }

    @Override
    public Value getIRCode() {
        return addExp.getIRCode();
    }
}
