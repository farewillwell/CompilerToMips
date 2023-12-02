package mid_end.llvm_ir.Instrs;

import back_end.Mips.AsmInstrs.LiAsm;
import back_end.Mips.AsmInstrs.MemAsm;
import back_end.Mips.MipsBuilder;
import back_end.Mips.Register;
import mid_end.llvm_ir.Constant;
import mid_end.llvm_ir.GlobalVar;
import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.Value;

public class StoreInstr extends Instr {
    // store value 是要被存的值
    public StoreInstr(Value storeValue, Value inTo) {
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
        if (paras.get(0) instanceof Constant) {
            new LiAsm(((Constant) paras.get(0)).getValue(), Register.T0);
        } else {
            int offset = MipsBuilder.MB.queryOffset(paras.get(0));
            new MemAsm(MemAsm.LW, Register.T0, Register.SP, offset);
        }
        // 找到指针的位置
        if (paras.get(1) instanceof GlobalVar) {
            new MemAsm(MemAsm.SW, Register.T0, ((GlobalVar) paras.get(1)).nameInMips(), 0);
        } else {
            int offset = MipsBuilder.MB.queryOffset(paras.get(1));
            // 获取指针存的值，就是变量的实际地址
            new MemAsm(MemAsm.LW, Register.T1, Register.SP, offset);
            // 把值存到实际地址里面
            new MemAsm(MemAsm.SW, Register.T0, Register.T1, 0);
        }
    }
}
