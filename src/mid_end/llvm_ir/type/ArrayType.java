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

    public LLVMType getOriginType() {
        if (memberType instanceof ArrayType) {
            return ((ArrayType) memberType).memberType;
        }
        return memberType;
    }

}
