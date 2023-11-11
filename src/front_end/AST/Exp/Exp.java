package front_end.AST.Exp;

import front_end.AST.Node;
import front_end.ErrorCollector;
import mid_end.llvm_ir.Value;

public class Exp extends Node {
    private final AddExp addExp;

    public Exp(AddExp addExp) {
        this.addExp = addExp;
    }

    @Override
    public void show() {
        addExp.show();
        System.out.println("<Exp>");
        super.show();
    }

    public int evaluate() {
        return addExp.evaluate();
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        super.checkError(errorCollector);
        addExp.checkError(errorCollector);
    }

    public int getDim() {
        return addExp.getDim();
    }

    public int getSec() {
        return addExp.getSec();
    }

    @Override
    public Value getIRCode() {
        return addExp.getIRCode();
    }

    @Override
    public int queryValue() {
        return addExp.queryValue();
    }
}
