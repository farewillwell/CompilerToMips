package mid_end.llvm_ir.Instrs;

import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.Value;

public class StoreInstr extends Instr {
    // store value 是要被存的值
    public StoreInstr(Value storeValue, Value inTo) {
        super();
        addValue(storeValue);
        addValue(inTo);
    }

    @Override
    public String toString() {
        return "store " + paras.get(0).type + " " + paras.get(0) +
                " , " + paras.get(1).type + " " + paras.get(1);
    }

    @Override
    public Value getAns() {
        throw new RuntimeException("get ans of a store instr");
    }
}
