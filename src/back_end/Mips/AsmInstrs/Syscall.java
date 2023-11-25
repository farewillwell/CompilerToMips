package back_end.Mips.AsmInstrs;

public class Syscall extends AsmInstr {
    public Syscall() {
        super("syscall");
    }

    public static final int PRINT_INT = 1;
    public static final int READ_INT = 5;
    public static final int EXIT = 10;
    public static final int PRINT_CHAR = 11;

    // 1 输出整数  $a0 : 待输出的
    // 4 输出字符串 $a0:待输出字符串地址
    // 5 读入整数 $a0 读入的整数
    // 10 exit
    // 11 输出char $a0 : char
    @Override
    public String toString() {
        return opcode;
    }
}
