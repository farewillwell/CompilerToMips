package back_end.Mips.AsmInstrs;

public class AnnotationAsm extends AsmInstr {
    private final String message;
    // 注解，防止生成的mips代码太难绷

    public AnnotationAsm(String messgae) {
        super("//");
        this.message = messgae;
    }

    @Override
    public String toString() {
        return opcode + " " + message;
    }
}
