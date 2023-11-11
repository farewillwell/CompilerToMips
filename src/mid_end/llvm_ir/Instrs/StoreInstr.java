package mid_end.llvm_ir.Instrs;

import mid_end.llvm_ir.GlobalVar;
import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.LocalVar;
import mid_end.llvm_ir.Value;
import mid_end.llvm_ir.type.LLVMType;

public class StoreInstr extends Instr {
    /*TODO 数组的赋值*/
    public StoreInstr(Value storeValue, Value in) {
        addValue(storeValue);
        addValue(in);
    }

    /**
     * TODO 注意，事实上，这里可能赋值的不止localVar，也有可能是别的
     */

    @Override
    public String toString() {
        String type1 = paras.get(1).type.toString();
        return "store " + paras.get(0).type.toString() + " " + paras.get(0) +
                " , " + type1 + " " + paras.get(1);
    }
}
