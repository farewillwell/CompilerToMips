package mid_end.llvm_ir.type;

public class PointerType extends LLVMType {
    public final LLVMType objType;

    public PointerType(LLVMType type) {
        this.objType = type;
    }

    @Override
    public String toString() {
        return objType.toString() + "*";
    }

    @Override
    public int getSize() {
        return 4;
    }
}
