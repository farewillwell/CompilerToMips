package front_end.AST;

import front_end.RW;
import front_end.Token;

public class TokenNode extends Node {
    private final Token token;

    public TokenNode(Token token) {
        this.token = token;
    }

    @Override
    public void show() {
        super.show();
        System.out.println(token.type + " " + token.getContent());
    }

    public String content() {
        return token.getContent();
    }

    public RW.TYPE type() {
        return token.type;
    }

    public int getLine() {
        return token.getLine();
    }
}
