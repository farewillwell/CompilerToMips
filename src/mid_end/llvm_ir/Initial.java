package mid_end.llvm_ir;

import mid_end.llvm_ir.type.ArrayType;
import mid_end.llvm_ir.type.BaseType;
import mid_end.llvm_ir.type.LLVMType;

import java.util.ArrayList;

public class Initial extends User {
    // 实际上逻辑无非两点：
    // 有初始内容，按初始内容来
    // 无初始内容，按照类型来，是int 就是0，不是int就是zeroInitial

    private final boolean initialExplicitly;

    private final ArrayList<Initial> initials;

    public final LLVMType containType;

    private final int value;

    public Initial(int value) {
        this.initials = new ArrayList<>();
        this.initialExplicitly = true;
        this.containType = BaseType.I32;
        this.value = value;
    }

    public Initial(LLVMType llvmType) {
        this.initials = new ArrayList<>();
        this.initialExplicitly = false;
        this.containType = llvmType;
        // 标记
        this.value = 0;
    }

    public Initial(LLVMType type, ArrayList<Initial> initials) {
        this.initials = new ArrayList<>(initials);
        this.initialExplicitly = true;
        this.containType = type;
        this.value = 0;
    }

    @Override
    public String toString() {
        if (!initialExplicitly) {
            if (containType != null && containType != BaseType.I32) {
                return containType + " zeroinitializer";
            } else {
                return BaseType.I32 + " 0";
            }
        }
        if (containType == BaseType.I32) {
            return containType + " " + value;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(containType);
            stringBuilder.append(" [");
            for (Initial initial : initials) {
                stringBuilder.append(initial.toString());
                if (initial != initials.get(initials.size() - 1)) stringBuilder.append(",");
            }
            stringBuilder.append("]");
            return stringBuilder.toString();
        }
    }

    public int getValue(int x) {
        if (!(containType instanceof ArrayType)) {
            throw new RuntimeException("getValue of not  a array with index");
        }
        if (initialExplicitly) {
            return initials.get(x).value;
        } else {
            return 0;
        }
    }

    public int getValue() {
        if (containType != BaseType.I32) {
            throw new RuntimeException("getValue of  a array");
        }
        if (initialExplicitly) {
            return value;
        } else {
            return 0;
        }
    }

    public int getValue(int x, int y) {
        if (!(containType instanceof ArrayType)) {
            throw new RuntimeException("getValue of not  a array with index");
        }
        if (initialExplicitly) {
            return initials.get(x).initials.get(y).value;
        } else {
            return 0;
        }
    }

}
