package mid_end.llvm_ir;


import mid_end.llvm_ir.type.LLVMType;

public class Value {

    public LLVMType type;

    public Value() {
        type = null;
    }

    public Value(LLVMType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public void genMipsCode() {

    }
}
