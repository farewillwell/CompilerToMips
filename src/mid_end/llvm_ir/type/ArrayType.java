package mid_end.llvm_ir.type;

public class ArrayType extends LLVMType {
    private final int length;
    public final LLVMType memberType;

    public ArrayType(int length, LLVMType type) {
        this.length = length;
        this.memberType = type;
    }

    @Override
    public String toString() {
        return "[" + length + " x " + memberType.toString() + "]";
    }

    @Override
    public int getSize() {
        return length * memberType.getSize();
    }
}
