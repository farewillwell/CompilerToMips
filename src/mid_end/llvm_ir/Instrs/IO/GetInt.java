package mid_end.llvm_ir.Instrs.IO;

import mid_end.llvm_ir.LocalVar;
import mid_end.llvm_ir.type.BaseType;

public class GetInt extends IOInstr {

    public GetInt() {
        paras.add(new LocalVar(BaseType.I32, false));
    }

    public static final String defineHead = "declare i32 @getint()\n";

    @Override
    public String toString() {
        return getAns().toString()+" = call i32 @getint()";
    }
}
