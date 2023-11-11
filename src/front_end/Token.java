package front_end;


public class Token {
    // 有两个不确定的，一个是id，一个是num，为此两种新建即可，剩下非纯数字的，都能直接匹配
    public final RW.TYPE type;

    private final int line;

    private final String content;

    public Token(RW.TYPE type, String string, int line) {
        this.type = type;
        content = string;
        this.line = line;
    }

    public String getContent() {
        return content;
    }

    public int getLine() {
        return line;
    }

}
