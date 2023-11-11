package mid_end.llvm_ir.type;

public class ArrayType extends LLVMType {
    private final int length;
    private final LLVMType memberType;

    public ArrayType(int length, LLVMType type) {
        this.length = length;
        this.memberType = type;
    }

    @Override
    public String toString() {
        return "[" + length + " x " + memberType.toString() + "]";
    }

    public LLVMType getElementType() {
        return memberType.getElementType();

    }
}
