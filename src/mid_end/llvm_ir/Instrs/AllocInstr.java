package mid_end.llvm_ir.Instrs;

import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.LocalVar;
import mid_end.llvm_ir.type.LLVMType;
import mid_end.llvm_ir.type.PointerType;

public class AllocInstr extends Instr {

    public AllocInstr(LLVMType type, boolean isConst) {
        super(type);
        this.addValue(new LocalVar(new PointerType(type), isConst));
    }

    @Override
    public String toString() {
        return paras.get(0).toString() + " = alloca " + type.toString();
    }
}
