package mid_end.llvm_ir;

import mid_end.llvm_ir.type.LLVMType;

public class LocalVar extends Value {

    private final String name;
    private final boolean isConst;

    private Initial constInitial = null;
    public LocalVar(LLVMType type, boolean isConst) {
        super(type);
        this.name = IRBuilder.IB.getLocalVarName();
        this.isConst = isConst;
    }

    public void setConstInitial(Initial initial) {
        if (!isConst) {
            throw new RuntimeException("give initial to not const");
        }
        this.constInitial = initial;
    }
    @Override
    public String toString() {
        return name;
    }
    // 一个localVar生成什么代码啊。。

    @Override
    public void genMipsCode() {
        //
    }

    public int getInitValue() {
        if (!isConst) {
            throw new RuntimeException("use not const localVal value");
        }
        return constInitial.getValue();
    }

    public int getInitValue(int x) {
        if (!isConst) {
            throw new RuntimeException("use not const localVal value");
        }
        return constInitial.getValue(x);
    }

    public int getInitValue(int x, int y) {
        if (!isConst) {
            throw new RuntimeException("use not const localVal value");
        }
        return constInitial.getValue(x, y);
    }
}
