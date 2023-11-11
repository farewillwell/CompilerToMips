package front_end.AST.Fun;

import front_end.AST.Node;
import front_end.AST.TokenNode;
import front_end.RW;

public class FuncType extends Node {
    private final TokenNode tokenNode;

    public FuncType(TokenNode tokenNode) {
        this.tokenNode = tokenNode;
    }

    @Override
    public void show() {
        super.show();
        tokenNode.show();
        System.out.println("<FuncType>");
    }

    public RW.TYPE getType() {
        return tokenNode.type();
    }
}
