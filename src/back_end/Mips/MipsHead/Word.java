package back_end.Mips.MipsHead;

public class Word extends Header {
    private final String name;
    private final int initValue;

    public Word(String name, int initValue) {
        this.name = name;
        this.initValue = initValue;
    }

    @Override
    public String toString() {
        return name + " : .word " + initValue;
    }
}
