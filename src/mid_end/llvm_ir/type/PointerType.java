package mid_end.llvm_ir.type;

public class PointerType extends LLVMType {
    private final LLVMType objType;

    public PointerType(LLVMType type) {
        this.objType = type;
    }

    @Override
    public String toString() {
        return objType.toString() + "*";
    }

    @Override
    public LLVMType getElementType() {
        return objType;
    }
}
