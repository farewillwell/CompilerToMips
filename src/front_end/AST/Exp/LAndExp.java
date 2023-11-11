package front_end.AST.Exp;

import front_end.AST.Node;

import java.util.ArrayList;

public class LAndExp extends Node {
    private final EqExp eqExp;
    private final ArrayList<EqExp> eqExps;

    public LAndExp(EqExp exp) {
        this.eqExp = exp;
        this.eqExps = new ArrayList<>();
    }

    public void addEqExp(EqExp exp) {
        eqExps.add(exp);
    }

    @Override
    public void show() {
        eqExp.show();
        System.out.println("<LAndExp>");
        for (EqExp exp : eqExps) {
            System.out.println("AND &&");
            exp.show();
            System.out.println("<LAndExp>");
        }
        super.show();
    }
}
