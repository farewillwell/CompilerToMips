package front_end;

public class Lexer {
    private final String source;
    // curPos应该始终指向下一个
    private int curPos;
    private char peek;

    private Token token;
    private int lineNum;

    public int getLineNum() {
        return lineNum;
    }

    public Lexer(String s) {
        this.source = s;
        curPos = 0;
        token = null;
        lineNum = 1;
        peek = source.charAt(curPos);
        gapWhite();
    }


    public boolean hasNext() {
        return curPos < source.length();
    }

    public Token getToken() {
        return token;
    }

    private void moveCur() {
        curPos++;
        if (curPos < source.length()) {
            peek = source.charAt(curPos);
        }

    }
    //  in windows ,there will be a \r , so we must do that
    private void gapWhite() {
        while (curPos < source.length()) {
            if (peek == ' ' || peek == '\t'||peek=='\r') {
                moveCur();
            } else if (peek == '\n') {
                lineNum++;
                moveCur();
            } else {
                return;
            }
        }
    }

    //注意一定要用builder改变字符串对象
    //别忘了解决注释还有" "
    public void next() {
        if (Character.isDigit(peek)) {
            int number = 0;
            do {
                number = 10 * number + Character.digit(peek, 10);
                moveCur();
            } while (Character.isDigit(peek) && curPos < source.length());
            token = new Num(number, lineNum);
        } else if (Character.isLetter(peek) || peek == '_') {
            StringBuilder builder = new StringBuilder();
            do {
                builder.append(peek);
                moveCur();
            } while ((Character.isLetterOrDigit(peek) || peek == '_') && curPos < source.length());
            String string = builder.toString();
            RW.TYPE type = RW.getType(string);
            token = new Token(type, string, lineNum);
        } else if (peek == '!' || peek == '<' || peek == '>' || peek == '=') {
            StringBuilder builder = new StringBuilder();
            builder.append(peek);
            moveCur();
            if (curPos < source.length() && peek == '=') {
                builder.append(peek);
                moveCur();
            }
            String string = builder.toString();
            RW.TYPE type = RW.getType(string);
            token = new Token(type, string, lineNum);
        } else if (peek == '&' || peek == '|') {
            if (curPos + 1 < source.length() && source.charAt(curPos + 1) == peek) {
                StringBuilder builder = new StringBuilder();
                builder.append(peek);
                moveCur();
                builder.append(peek);
                moveCur();
                String string = builder.toString();
                RW.TYPE type = RW.getType(string);
                token = new Token(type, string, lineNum);
            } else {
                throw new RuntimeException("there is only one '" + peek + "' in line " + lineNum);
            }
        } else if (peek == '"') {
            StringBuilder builder = new StringBuilder();
            do {
                builder.append(peek);
                moveCur();
            } while (peek != '"' && curPos < source.length());
            if (curPos == source.length()) {
                throw new RuntimeException("there is only one \" in a printf ");
            }
            builder.append(peek);
            moveCur();
            String string = builder.toString();
            token = new Token(RW.TYPE.STRCON, string, lineNum);
        } else if (peek == '/') {
            if (curPos + 1 < source.length() && source.charAt(curPos + 1) == '/') {
                do {
                    moveCur();
                } while (curPos < source.length() && peek != '\n');
                token = new Token(RW.TYPE.NOTE, "//", lineNum);
            } else if (curPos + 1 < source.length() && source.charAt(curPos + 1) == '*') {
                moveCur();
                do {
                    moveCur();
                    if (peek == '*' && curPos + 1 < source.length() && source.charAt(curPos + 1) == '/') {
                        moveCur();
                        moveCur();
                        break;
                    }
                } while (curPos < source.length());
                token = new Token(RW.TYPE.NOTE, "/**/", lineNum);
            } else {
                String builder = String.valueOf(peek);
                moveCur();
                RW.TYPE type = RW.getType(builder);
                token = new Token(type, builder, lineNum);
            }
        } else {
            String builder = String.valueOf(peek);
            moveCur();
            RW.TYPE type = RW.getType(builder);
            token = new Token(type, builder, lineNum);
        }
        gapWhite();
    }


}
