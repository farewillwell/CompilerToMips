package back_end.Mips.AsmInstrs;

public class AnnotationMips extends MipsInstr {
    private final String message;
    // 注解，防止生成的mips代码太难绷

    public AnnotationMips(String messgae) {
        super("\n#");
        this.message = messgae;
    }

    @Override
    public String toString() {
        return opcode + " " + message;
    }
}
