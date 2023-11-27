package back_end.Mips.MipsHead;

public class Asciiz extends Header {
    private final String name;
    private final String content;

    public Asciiz(String name, String content) {
        this.name = name;
        this.content = content;
    }

    @Override
    public String toString() {
        return name + ": .asciiz \"" + content + "\"";
    }
}
