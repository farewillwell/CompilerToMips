package front_end.AST.Exp;

import front_end.AST.Node;
import front_end.AST.TokenNode;
import mid_end.llvm_ir.Constant;
import mid_end.llvm_ir.Value;

public class Number extends Node {
    private final TokenNode tokenNode;

    public Number(TokenNode tokenNode) {
        this.tokenNode = tokenNode;
    }

    @Override
    public void show() {
        tokenNode.show();
        System.out.println("<Number>");
        super.show();
    }

    public int evaluate() {
        return Integer.parseInt(tokenNode.content());
    }

    public int getDim() {
        return 0;
    }

    public int getSec() {
        return 0;
    }

    @Override
    public Value getIRCode() {
        return new Constant(Integer.parseInt(tokenNode.content()));
    }

    @Override
    public int queryValue() {
        return Integer.parseInt(tokenNode.content());
    }


}
