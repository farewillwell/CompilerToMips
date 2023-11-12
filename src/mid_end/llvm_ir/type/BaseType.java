package mid_end.llvm_ir.type;

public class BaseType extends LLVMType {

    private final int width;

    public BaseType(int width) {
        this.width = width;
    }

    public static final BaseType I1 = new BaseType(1);

    public static final BaseType I8 = new BaseType(8);

    public static final BaseType I32 = new BaseType(32);

    public static final BaseType Void = new BaseType(0);


    @Override
    public String toString() {
        if (width == 0) {
            return "void";
        } else if (width == 1) {
            return "i1";
        } else if (width == 8) {
            return "i8";
        } else if (width == 32) {
            return "i32";
        } else {
            throw new RuntimeException("Invalid type " + width);
        }
    }
}
