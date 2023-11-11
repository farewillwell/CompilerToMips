package front_end;

public class Num extends Token {
    private final int value;

    public Num(int value, int line) {
        super(RW.TYPE.INTCON,String.valueOf(value), line);
        this.value = value;
    }
}
