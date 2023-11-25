package mid_end.llvm_ir;

import mid_end.llvm_ir.type.BaseType;

public class Constant extends Value {


    private final int value;

    public Constant(int value) {
        this.type = BaseType.I32;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public int getValue() {
        return value;
    }
}
