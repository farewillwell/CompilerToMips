package mid_end.llvm_ir.Instrs;

import back_end.Mips.AsmInstrs.MemMips;
import back_end.Mips.Register;
import mid_end.llvm_ir.GlobalVar;
import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.Value;

public class StoreIr extends Instr {
    // store value 是要被存的值
    public StoreIr(Value storeValue, Value inTo) {
        super();
        addValue(storeValue);
        addValue(inTo);
    }

    public boolean hasDef(Value value) {
        return paras.get(1) == value;
    }

    public Value getDstPointer() {
        return paras.get(1);
    }

    public Value getStoreInValue() {
        return paras.get(0);
    }

    @Override
    public String toString() {
        return "store " + paras.get(0).type + " " + paras.get(0) +
                " , " + paras.get(1).type + " " + paras.get(1);
    }

    @Override
    public Value getAns() {
        return null;
    }

    @Override
    public void genMipsCode() {
        super.genMipsCode();
        Register store = Instr.moveValueIntoReg(Register.T0, getStoreInValue());
        // 找到指针的位置
        if (paras.get(1) instanceof GlobalVar) {
            new MemMips(MemMips.SW, store, ((GlobalVar) paras.get(1)).nameInMips(), 0);
        } else {
            Register point = Instr.moveValueIntoReg(Register.T1, paras.get(1));
            // 把值存到实际地址里面
            new MemMips(MemMips.SW, store, point, 0);
        }
    }
}
