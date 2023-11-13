package mid_end.llvm_ir.Instrs;

import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.LocalVar;
import mid_end.llvm_ir.Value;
import mid_end.llvm_ir.type.BaseType;

public class ZextInstr extends Instr {
    // extend 专门用于扩展,压缩的可以用icmp和0来表示

    public ZextInstr(BaseType targetType, Value beChangedValue) {
        addValue(beChangedValue);
        addValue(new LocalVar(targetType, false));
    }
    @Override
    public String toString() {
        return getAns() + " = zext " + paras.get(0).type + " " + paras.get(0) + " to " + getAns().type;
    }
}
