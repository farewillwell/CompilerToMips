package mid_end.llvm_ir.Instrs;

import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.Value;
import mid_end.llvm_ir.type.BaseType;

public class ReturnInstr extends Instr {
    private final BaseType retType;

    private final Value retValue;

    public ReturnInstr(Value value) {
        super();
        retType = BaseType.I32;
        retValue = value;
    }

    public ReturnInstr() {
        retType = BaseType.Void;
        retValue = null;
    }

    @Override
    public String toString() {
        // StringBuilder builder =new StringBuilder();
        if (retType.equals(BaseType.I32)) {
            assert retValue != null;
            return "ret i32 " + retValue.toString();
        } else {
            return "ret void";
        }
    }
}
