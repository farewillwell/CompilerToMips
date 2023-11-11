package front_end.AST.Exp;

import front_end.AST.Node;
import front_end.AST.TokenNode;

import java.util.ArrayList;

public class EqExp extends Node {
    private final RelExp relExp;
    private final ArrayList<TokenNode> tokenNodes;
    private final ArrayList<RelExp> otherRelExps;

    public EqExp(RelExp relExp) {
        this.relExp = relExp;
        this.otherRelExps = new ArrayList<>();
        this.tokenNodes = new ArrayList<>();
    }

    public void addRelExp(TokenNode tokenNode, RelExp relExp) {
        tokenNodes.add(tokenNode);
        otherRelExps.add(relExp);
    }

    @Override
    public void show() {
        relExp.show();
        System.out.println("<EqExp>");
        for (int i = 0; i < otherRelExps.size(); i++) {
            tokenNodes.get(i).show();
            otherRelExps.get(i).show();
            System.out.println("<EqExp>");
        }

        super.show();
    }
}
