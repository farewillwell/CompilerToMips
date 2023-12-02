package back_end.Mips;

import mid_end.llvm_ir.Value;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class VarManager {
    // 这个管理的是llvm的Point所指向的value，比如alloc一个新的整数，那么应该用reg优先。

    public final HashSet<Register> regUsed;
    public final LinkedList<Register> usableRegs;

    public final HashMap<Value, MipsSymbol> memMap;
    private int virtualSpOffsetFromRealSp;

    // 字节数，一个i32有4个字节

    public boolean hasUsableReg() {
        return usableRegs.size() != 0;
    }

    public int allocOnStack(int byteSize) {
        if (byteSize % 4 != 0) {
            throw new RuntimeException();
        }
        virtualSpOffsetFromRealSp -= byteSize;
        return virtualSpOffsetFromRealSp;
    }

    public Register allocOnReg() {
        return usableRegs.removeFirst();
    }

    // 每次进入到一个新函数都要初始化usableReg
    public VarManager() {
        regUsed = new HashSet<>();
        usableRegs = new LinkedList<>();
        memMap = new HashMap<>();
        for (int i = 11; i <= 27; i++) {
            usableRegs.add(Register.getWithIndex(i));
        }
    }

    public void addLocalSymbol(MipsSymbol mipsSymbol) {
        this.memMap.put(mipsSymbol.value, mipsSymbol);
    }

    public boolean valueInReg(Value value) {
        return memMap.get(value).storeInReg;
    }

    public int queryOffset(Value value) {
        if (!memMap.containsKey(value)) {
            System.out.println(value);
        }
        return memMap.get(value).offset;
    }

    public Register queryReg(Value value) {
        return memMap.get(value).register;
    }

    // 如何在从一个函数返回的时候搞清楚剩下可以用的?

    public int getVir() {
        return virtualSpOffsetFromRealSp;
    }

    public boolean hasDefine(Value value) {
        return memMap.containsKey(value);
    }
}
