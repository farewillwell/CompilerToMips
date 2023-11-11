package mid_end.llvm_ir;

import mid_end.llvm_ir.type.LLVMType;

public class GlobalVar extends Value {
    // 变量似乎不用考虑是否是const了？在处理的时候不需要特殊考虑，在输出的时候需要考虑。
    private final boolean isConst;
    private final Initial initial;
    private final String name;

    public GlobalVar(String name, LLVMType type, Initial initial, boolean isConst) {
        super(type);
        this.name = IRBuilder.IB.getGlobalVarName(name);
        this.initial = initial;
        this.isConst = isConst;
    }

    public String forShow() {
        return name +
                " = dso_local " +
                (isConst ? "constant " : "global ") +
                initial.toString() + "\n";
    }

    @Override
    public String toString() {
        return name;
    }

    public int getInitValue() {
        return initial.getValue();
    }
}
