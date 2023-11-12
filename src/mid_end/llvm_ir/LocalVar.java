package mid_end.llvm_ir;

import mid_end.llvm_ir.type.LLVMType;

public class LocalVar extends Value {

    private final String name;
    private final boolean isConst;

    public LocalVar(LLVMType type, boolean isConst) {
        super(type);
        this.name = IRBuilder.IB.getLocalVarName();
        this.isConst = isConst;
    }

    @Override
    public String toString() {
        return name;
    }
}
