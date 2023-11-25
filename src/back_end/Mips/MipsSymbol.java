package back_end.Mips;

import mid_end.llvm_ir.Value;

public class MipsSymbol {
    // 变量的存储表,比如%v1,...%30,他们导致寄存器不够，怎么办？
    // 如果有寄存器的话就分配一个寄存器给他，没有的话就存到内存里面.
    // 考虑到地址这个不会变换，因此不需要脏位这个东西
    public final Value value;
    public boolean storeInReg;
    public Register register;
    public int offset;

    // offset 应该是相对于当前栈底的偏移量(高位),就是这个变量的首地址
    // | high ------------->>>>--------------------- low
    // sp --- always the be the bottom , so the offset must be negative
    public MipsSymbol(Value value, Register register) {
        this.value = value;
        storeInReg = true;
        this.register = register;
        this.offset = Integer.MAX_VALUE;
    }

    public MipsSymbol(Value value, int offset) {
        this.value = value;
        this.storeInReg = false;
        this.register = null;
        this.offset = offset;
    }

}
