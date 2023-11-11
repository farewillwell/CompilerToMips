package front_end.AST.Exp;

import front_end.AST.Node;
import front_end.AST.TokenNode;

import java.util.ArrayList;

public class RelExp extends Node {
    private final AddExp addExp;
    private final ArrayList<TokenNode> tokenNodes;
    private final ArrayList<AddExp> otherAddExps;

    public RelExp(AddExp addExp) {
        this.addExp = addExp;
        this.otherAddExps = new ArrayList<>();
        this.tokenNodes = new ArrayList<>();
    }

    public void addAddExp(TokenNode tokenNode, AddExp AddExp) {
        tokenNodes.add(tokenNode);
        otherAddExps.add(AddExp);
    }

    @Override
    public void show() {
        addExp.show();
        System.out.println("<RelExp>");
        for (int i = 0; i < otherAddExps.size(); i++) {
            tokenNodes.get(i).show();
            otherAddExps.get(i).show();
            System.out.println("<RelExp>");
        }

        super.show();
    }
}
