package mid_end.llvm_ir;

import mid_end.llvm_ir.type.LLVMType;

public class Param extends Value {

    private final String name;

    public Value replacer = null;

    public Param(LLVMType type) {
        super(type);
        this.name = IRBuilder.IB.getParamName();
    }

    @Override
    public String toString() {
        if (replacer != null) {
            return replacer.toString();
        }
        return name;
    }

    // 被func所包含，因此不需要进行getMipsCode
}
