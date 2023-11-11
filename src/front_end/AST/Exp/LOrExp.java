package front_end.AST.Exp;

import front_end.AST.Node;

import java.util.ArrayList;

public class LOrExp extends Node {
    private final LAndExp lAndExp;
    private final ArrayList<LAndExp> lAndExps;

    public LOrExp(LAndExp exp) {
        this.lAndExp = exp;
        this.lAndExps = new ArrayList<>();
    }

    public void addLAndExp(LAndExp exp) {
        lAndExps.add(exp);
    }
    public void show() {
        lAndExp.show();
        System.out.println("<LOrExp>");
        for (LAndExp exp : lAndExps) {
            System.out.println("OR ||");
            exp.show();
            System.out.println("<LOrExp>");
        }
        super.show();
    }
}
