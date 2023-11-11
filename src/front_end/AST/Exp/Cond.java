package front_end.AST.Exp;

import front_end.AST.Node;

public class Cond extends Node {

    private final LOrExp lOrExp;

    public Cond(LOrExp lOrExp) {
        this.lOrExp = lOrExp;
    }

    @Override
    public void show() {
        lOrExp.show();
        System.out.println("<Cond>");
        super.show();
    }
}
