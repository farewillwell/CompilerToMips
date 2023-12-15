package mid_end.llvm_ir.Instrs;

import back_end.Mips.AsmInstrs.MemMips;
import back_end.Mips.MipsBuilder;
import back_end.Mips.Register;
import mid_end.llvm_ir.GlobalVar;
import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.LocalVar;
import mid_end.llvm_ir.Value;
import mid_end.llvm_ir.type.PointerType;

public class LoadIr extends Instr {
    public LoadIr(Value pointer) {
        super(((PointerType) pointer.type).objType);
        addValue(pointer);
        // 其获取的值类型是这个变量指针所指的值类型
        setAns(new LocalVar(((PointerType) pointer.type).objType, false));
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

    // 事实上,这个还剩下的只有和全局变量或者数组打交道的了
    // 为了节约速度,应当直接专门为这个开一种手段
    // 同时,load的结果必然是localVal,而不是para或者什么global
    @Override
    public void genMipsCode() {
        super.genMipsCode();
        Register register = Instr.targetSRegorNull(getAns());
        if (paras.get(0) instanceof GlobalVar) {
            // 从标签地址里面找值存到分配的值的空间里
            if (register != null) {
                new MemMips(MemMips.LW, register, ((GlobalVar) paras.get(0)).nameInMips(), 0);
                MipsBuilder.MB.storeInReg(getAns(), register);
            } else {
                new MemMips(MemMips.LW, Register.T0, ((GlobalVar) paras.get(0)).nameInMips(), 0);
                int offset = MipsBuilder.MB.queryOffset(getAns());
                new MemMips(MemMips.SW, Register.T0, Register.SP, offset);
            }
        } else {
            // 取出这个local指针的值到某个寄存器
            Register pointer = Instr.moveValueIntoReg(Register.T0, paras.get(0));
            if (register != null) {
                new MemMips(MemMips.LW, register, pointer, 0);
                MipsBuilder.MB.storeInReg(getAns(), register);
            } else {
                new MemMips(MemMips.LW, Register.T0, pointer, 0);
                //  把取到的值存回去
                int offset = MipsBuilder.MB.queryOffset(getAns());
                new MemMips(MemMips.SW, Register.T0, Register.SP, offset);
            }
        }
    }
}
