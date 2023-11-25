package mid_end.llvm_ir.type;

import mid_end.llvm_ir.Constant;
import mid_end.llvm_ir.IRBuilder;
import mid_end.llvm_ir.Instrs.IcmpInstr;
import mid_end.llvm_ir.Instrs.ZextInstr;
import mid_end.llvm_ir.Value;

public class BaseType extends LLVMType {

    private final int width;

    public BaseType(int width) {
        this.width = width;
    }

    public static final BaseType I1 = new BaseType(1);

    public static final BaseType I8 = new BaseType(8);

    public static final BaseType I32 = new BaseType(32);

    public static final BaseType Void = new BaseType(0);

    public static Value ensureReturnType(BaseType needType, Value value) {
        if (!(value.type instanceof BaseType)) {
            throw new RuntimeException("try to change a unBase type");
        }
        BaseType valueType = (BaseType) value.type;
        if (valueType == needType) {
            return value;
        }
        // 默认只会 32 1 互相转 , 因此可以直接比较
        if (needType.width < valueType.width) {
            if (needType.width != 1) {
                throw new RuntimeException("turn a large with into smaller but not 1");
            }
            IcmpInstr icmpInstr = new IcmpInstr(IcmpInstr.NE, value, new Constant(0));
            IRBuilder.IB.addInstrForBlock(icmpInstr);
            return icmpInstr.getAns();
        } else {
            ZextInstr zextInstr = new ZextInstr(needType, value);
            IRBuilder.IB.addInstrForBlock(zextInstr);
            return zextInstr.getAns();
        }
    }


    @Override
    public String toString() {
        if (width == 0) {
            return "void";
        } else if (width == 1) {
            return "i1";
        } else if (width == 8) {
            return "i8";
        } else if (width == 32) {
            return "i32";
        } else {
            throw new RuntimeException("Invalid type " + width);
        }
    }

    @Override
    public int getSize() {
        return width / 8;
    }
}
