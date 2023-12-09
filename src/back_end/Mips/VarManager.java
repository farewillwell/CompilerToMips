package back_end.Mips;

import back_end.Mips.AsmInstrs.MemAsm;
import mid_end.llvm_ir.Value;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class VarManager {


    // 变量即将使用的Reg,可以重叠,但是一个变量只能用一个!!!
    // 这个一旦加上就不修改了
    public final HashMap<Value, Register> varSReg;
    // 当前已经使用的reg的使用方式,注意,这个只是用来在进入函数之后的时候回归位置使用的
    public final HashMap<Register, Value> regUsed;
    public final LinkedList<Register> usableRegs;

    public final HashMap<Value, MipsSymbol> memMap;
    private int virtualSpOffsetFromRealSp;

    // 字节数，一个i32有4个字节

    public boolean hasUsableReg() {
        return regUsed.size() != usableRegs.size();
    }

    public int allocOnStack(int byteSize) {
        if (byteSize % 4 != 0) {
            throw new RuntimeException();
        }
        virtualSpOffsetFromRealSp -= byteSize;
        return virtualSpOffsetFromRealSp;
    }

    public void allocOnReg(Value value) {
        // 任意选取一个可用的
        LinkedList<Register> canBeUsed = new LinkedList<>(usableRegs);
        canBeUsed.removeAll(regUsed.keySet());
        Register register = canBeUsed.getFirst();
        regUsed.put(register, value);
        varSReg.put(value, register);
        System.out.println(value + " -> " + register);
    }

    // 每次进入到一个新函数都要初始化usableReg
    public VarManager() {
        regUsed = new HashMap<>();
        usableRegs = new LinkedList<>();
        varSReg = new HashMap<>();
        memMap = new HashMap<>();
        for (int i = 11; i <= 28; i++) {
            usableRegs.add(Register.getWithIndex(i));
        }
    }

    public void addLocalSymbol(MipsSymbol mipsSymbol) {
        this.memMap.put(mipsSymbol.value, mipsSymbol);
    }

    public int queryOffset(Value value) {
        if (!memMap.containsKey(value)) {
            System.out.println(value);
        }
        return memMap.get(value).offset;
    }

    public int getVir() {
        return virtualSpOffsetFromRealSp;
    }

    public void setVir(int cur) {
        virtualSpOffsetFromRealSp = cur;
    }

    // 重大bug:错误解放寄存器
    // 例如我去了一个不含这个的子节点,把这个解散了,那么这个list就永远的含了这个
    // 当我退回的时候,这个添加上的解散可以在分配栈上被满足,但是在这里无法被消除
    public void releaseReg(Register register) {
        if (register == null) {
            System.out.println((Object) null);
            return;
        }
        System.out.println("remove " + register + " <--> " + regUsed.get(register));
        regUsed.remove(register);
        // 不可以重复添加,因此要检查一下里面是不是已经有了,就是重复释放了
    }

    // 将所有使用的寄存器放回到他们对应的位置
    // 所有使用的寄存器吗?假如以后在活跃的没有那么多了,那还需要全存起来吗?
    // 目前先存起来吧
    // 但是需要谨记的是,regUsed仅仅记录的是分配完了之后寄存器的使用
    // 而不是调用这个函数时的使用
    // 因此我就需要记录一下当前谁是真正的使用者
    // 因此要遍历所有symbol
    // 而且这时候就换走了,下一次回来还是这些人在,所以不需要进行符号更换
    public void regBackToMem() {
        for (Value value : memMap.keySet()) {
            if (memMap.get(value).register != null) {
                int offset = memMap.get(value).offset;
                new MemAsm(MemAsm.SW, memMap.get(value).register, Register.SP, offset);
            }
        }
    }

    public void memBackToReg() {
        for (Value value : memMap.keySet()) {
            if (memMap.get(value).register != null) {
                int offset = memMap.get(value).offset;
                new MemAsm(MemAsm.LW, memMap.get(value).register, Register.SP, offset);
            }
        }
    }
}
