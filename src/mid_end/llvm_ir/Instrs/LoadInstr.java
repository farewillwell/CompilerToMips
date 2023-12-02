package mid_end.llvm_ir.Instrs;

import back_end.Mips.AsmInstrs.MemAsm;
import back_end.Mips.MipsSymbol;
import back_end.Mips.MipsBuilder;
import back_end.Mips.Register;
import mid_end.llvm_ir.GlobalVar;
import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.LocalVar;
import mid_end.llvm_ir.Value;
import mid_end.llvm_ir.type.PointerType;

public class LoadInstr extends Instr {
    public LoadInstr(Value pointer) {
        super(((PointerType) pointer.type).objType);
        addValue(pointer);
        // 其获取的值类型是这个变量指针所指的值类型
        setAns(new LocalVar(((PointerType) pointer.type).objType, false));
    }

    public boolean hasUse(Value pointer) {
        return paras.get(0) == pointer;
    }

    public Value getFromPointer() {
        return paras.get(0);
    }


    @Override
    public String toString() {
        String from = paras.get(0).type +
                " " + paras.get(0).toString();
        return getAns().toString() + " = load " + type + ", " + from;
    }

    @Override
    public void genMipsCode() {
        super.genMipsCode();
        if (paras.get(0) instanceof GlobalVar) {
            // 分配值的空间
            int offset = MipsBuilder.MB.allocOnStack(paras.get(0).type.getSize());
            // 从标签地址里面找值
            new MemAsm(MemAsm.LW, Register.T0, ((GlobalVar) paras.get(0)).nameInMips(), 0);
            // 存到分配的值的空间里
            new MemAsm(MemAsm.SW, Register.T0, Register.SP, offset);
            MipsBuilder.MB.addVarSymbol(new MipsSymbol(getAns(), offset));
        } else {
            // 首先找到指针的位置
            int offset = MipsBuilder.MB.queryOffset(paras.get(0));
            // 获取指针的值
            new MemAsm(MemAsm.LW, Register.T0, Register.SP, offset);
            // 从指针的值获取变量的值，指针的值就是变量的位置
            new MemAsm(MemAsm.LW, Register.T0, Register.T0, 0);
            // 新开一个地址存load出来的值
            int valueOffset = MipsBuilder.MB.allocOnStack(type.getSize());
            new MemAsm(MemAsm.SW, Register.T0, Register.SP, valueOffset);
            MipsSymbol mipsSymbol = new MipsSymbol(getAns(), valueOffset);
            MipsBuilder.MB.addVarSymbol(mipsSymbol);
        }
    }
}
