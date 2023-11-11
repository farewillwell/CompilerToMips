package mid_end.llvm_ir;

import mid_end.llvm_ir.type.LLVMType;

public class Param extends Value {

    private final String name;

    public Param(LLVMType type) {
        super(type);
        this.name = IRBuilder.IB.getParamName();
    }

    @Override
    public String toString() {
        return name;
    }
}
