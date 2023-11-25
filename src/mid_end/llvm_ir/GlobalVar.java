package mid_end.llvm_ir;

import back_end.Mips.MipsBuilder;
import back_end.Mips.MipsHead.Space;
import back_end.Mips.MipsHead.Word;
import mid_end.llvm_ir.type.BaseType;
import mid_end.llvm_ir.type.LLVMType;
import mid_end.llvm_ir.type.PointerType;

import java.util.ArrayList;

public class GlobalVar extends Value {
    // 变量似乎不用考虑是否是const了？在处理的时候不需要特殊考虑，在输出的时候需要考虑。
    private final boolean isConst;
    private final Initial initial;
    public final String name;
    private final int globalOrder;
    // 为了全局变量输出地址而采用
    // 如果用原名的话, gONE,会被识别错误，因此有必要更换

    public GlobalVar(String name, LLVMType type, Initial initial, boolean isConst) {
        super(type);
        this.name = IRBuilder.IB.getGlobalVarName(name);
        this.initial = initial;
        this.isConst = isConst;
        this.globalOrder = IRBuilder.IB.globalCnt;
    }

    public String forShow() {
        return name +
                " = dso_local " +
                (isConst ? "constant " : "global ") +
                initial.toString() + "\n";
    }

    @Override
    public String toString() {
        return name;
    }

    public int getInitValue() {
        return initial.getValue();
    }

    public int getInitValue(int x) {
        return initial.getValue(x);
    }

    public int getInitValue(int x, int y) {
        return initial.getValue(x, y);
    }

    public String nameInMips() {
        return "g" + globalOrder;
    }

    @Override
    public void genMipsCode() {
        // 全局变量这里 ,type 存的必然是一个指针类型
        if (((PointerType) type).objType instanceof BaseType) {
            MipsBuilder.MB.addHead(new Word(nameInMips(), getInitValue()));
        } else {
            if (initial.initialExplicitly) {
                ArrayList<Integer> initValues = initial.makeInitMipsCode();
                MipsBuilder.MB.addHead(new Word(nameInMips(), initValues));
            } else {
                MipsBuilder.MB.addHead(new Space(nameInMips(), ((PointerType) type).objType.getSize()));
            }
        }
    }
}
