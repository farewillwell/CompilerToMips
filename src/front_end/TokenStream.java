package front_end;

import java.util.ArrayList;

public class TokenStream {
    private final ArrayList<Token> tokenList;
    private int pos;


    public TokenStream(Lexer lexer) {
        pos = 0;
        tokenList = new ArrayList<>();
        while (lexer.hasNext()) {
            lexer.next();
            if (lexer.getToken().type != RW.TYPE.NOTE) {
                tokenList.add(lexer.getToken());
            }
        }
    }

    public void move() {
        //System.out.println(now().getContent());
        pos++;
    }

    public Token now() {
        return tokenList.get(pos);
    }

    public int getPos() {
        return pos;
    }

    public void backToMark(int mark) {
        pos = mark;
    }

    public Token next() {
        return tokenList.get(pos + 1);
    }

    public Token preNext() {
        return tokenList.get(pos + 2);
    }


    public void checkError(RW.TYPE type) {
        if (now().type != type) {
            throw new RuntimeException("we want " + type + " but we got a " + now().type
                    + " " + now().getContent() + " at " + now().getLine());
        }
    }
    //针对在语法中应有的终结符，使用这个来，否则会爆异常


    public int lastTokenLineNum() {
        return tokenList.get(pos - 1).getLine();
    }
}
