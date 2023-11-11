package mid_end.llvm_ir.Instrs.IO;

import mid_end.llvm_ir.Value;

public class PutCh extends IOInstr {

    public PutCh(Value value) {
        super();
        paras.add(value);
    }

    public static final String defineHead = "declare void @putch(i32)\n";

    @Override
    public String toString() {
        return "call void @putch(" + getAns().toString() + ")";
    }
}
