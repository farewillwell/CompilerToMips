package back_end.Mips.MipsHead;

public class Space extends Header {
    private final String name;
    private final int size;

    public Space(String name, int length) {
        // length 是 数组的长度
        this.name = name;
        this.size = length * 4;
    }

    public Space(String name, int firstLength, int secondLength) {
        this.name = name;
        this.size = 4 * firstLength * secondLength;
    }

    @Override
    public String toString() {
        return name + ": .space " + size;
    }
}
