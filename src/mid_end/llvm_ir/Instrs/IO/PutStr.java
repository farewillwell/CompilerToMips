package mid_end.llvm_ir.Instrs.IO;

import mid_end.llvm_ir.Value;

public class PutStr extends IOInstr {
    public PutStr(Value value) {
        super();
        paras.add(value);
    }

    public static final String defineHead = "declare void @putstr(i8*)\n";

    @Override
    public String toString() {

        /*TODO */
        return "";
    }
}
