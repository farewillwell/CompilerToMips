package back_end.Mips.MipsHead;

public class Space extends Header {
    private final String name;
    private final int size;

    public Space(String name, int length) {
        // length 是 数组的长度
        this.name = name;
        this.size = length;
        // 传进来的时候默认的字节数字已经得到了，所以根本不用乘4
    }

    @Override
    public String toString() {
        return name + ": .space " + size;
    }
}
