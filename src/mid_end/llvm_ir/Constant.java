package mid_end.llvm_ir;

import mid_end.llvm_ir.type.BaseType;

public class Constant extends Value {

    public final boolean isUndefine;


    private final int value;

    public Constant(int value) {
        this.type = BaseType.I32;
        this.value = value;
        isUndefine = false;
    }

    public Constant() {
        this.type = BaseType.I32;
        this.value = 0;
        isUndefine = true;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public int getValue() {
        return value;
    }
}
