package mid_end.llvm_ir;

import mid_end.llvm_ir.type.BaseType;
import mid_end.llvm_ir.type.LLVMType;

import java.util.ArrayList;

public class Initial extends User {

    private final boolean isZero;

    private final ArrayList<Initial> initials;

    public final LLVMType containType;

    private final int value;

    public Initial(int value) {
        this.initials = new ArrayList<>();
        this.isZero = false;
        this.containType = BaseType.I32;
        this.value = value;
    }

    public Initial() {
        this.initials = new ArrayList<>();
        this.isZero = true;
        this.containType = null;
        // 标记
        this.value = 0;
    }

    public Initial(LLVMType type, ArrayList<Initial> initials) {
        this.initials = new ArrayList<>(initials);
        this.isZero = false;
        this.containType = type;
        this.value = 0;
    }

    @Override
    public String toString() {
        if (isZero) {
            if (containType != null) {
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

    public int getValue() {
        if (containType != BaseType.I32) {
            throw new RuntimeException("getValue of a array");
        }
        return value;
    }
}
