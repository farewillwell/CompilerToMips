package mid_end.llvm_ir.Instrs.IO;

import mid_end.llvm_ir.Value;

public class PutInt extends IOInstr {
    public PutInt(Value value) {
        super();
        paras.add(value);
    }

    public static final String defineHead = "declare void @putint(i32)\n";

    @Override
    public String toString() {
        return "call void @putint(" + getAns().type + " " + getAns().toString() + ")";
    }
}
