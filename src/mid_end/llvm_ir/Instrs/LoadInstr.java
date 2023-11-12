package mid_end.llvm_ir.Instrs;

import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.LocalVar;
import mid_end.llvm_ir.Value;
import mid_end.llvm_ir.type.PointerType;

public class LoadInstr extends Instr {
    public LoadInstr(Value pointer) {
        super(((PointerType) pointer.type).objType);
        addValue(pointer);
        // 其获取的值类型是这个变量指针所指的值类型
        addValue(new LocalVar(((PointerType) pointer.type).objType, false));
    }


    @Override
    public String toString() {
        String from = paras.get(0).type +
                " " + paras.get(0).toString();
        return getAns().toString() + " = load " + type + ", " + from;

    }
}
