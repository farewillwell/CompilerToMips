package front_end.AST.Exp;

import front_end.AST.Node;
import front_end.AST.TokenNode;
import front_end.RW;

public class UnaryOp extends Node {
    private final TokenNode tokenNode;

    public UnaryOp(TokenNode tokenNode) {
        this.tokenNode = tokenNode;
    }

    @Override
    public void show() {
        tokenNode.show();
        System.out.println("<UnaryOp>");
        super.show();
    }

    public int evaluate() {
        if (tokenNode.type() == RW.TYPE.PLUS) {
            return 1;
        } else {
            return -1;
        }
    }

    public boolean isNegative() {
        return tokenNode.type() == RW.TYPE.MINU;
    }

    public boolean isNot() {
        return tokenNode.type() == RW.TYPE.NOT;
    }

    public boolean isPositive() {
        return tokenNode.type() == RW.TYPE.PLUS;
    }
}
