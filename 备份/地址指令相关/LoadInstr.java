package mid_end.llvm_ir.Instrs;

import mid_end.llvm_ir.GlobalVar;
import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.LocalVar;
import mid_end.llvm_ir.Value;
import mid_end.llvm_ir.type.ArrayType;
import mid_end.llvm_ir.type.LLVMType;
import mid_end.llvm_ir.type.PointerType;

import java.util.ArrayList;

public class LoadInstr extends Instr {
    // 不应该是传入名字，应该是传入要调用的指针的值
    // public boolean isFromGlobalArray;

    // 从非全局数组中取值
    // load Instr 的type是它from的东西指到的东西
    public LoadInstr(Value pointer) {
        super(((PointerType) pointer.type).objType);
        // isFromGlobalArray = false;
        addValue(pointer);
        // 其获取的值类型是这个变量指针所指的值类型
        addValue(new LocalVar(((PointerType) pointer.type).objType, false));
    }

    // 从全局数组中取值

    /*
    public LoadInstr(GlobalVar globalPoint, ArrayList<Value> offsets) {
        this.isFromGlobalArray = true;
        addValue(globalPoint);
        for (Value value : offsets) {
            addValue(value);
        }
        // 传进来是一个指针，而且所指的必然是一个数组
        ArrayType arrayType = (ArrayType) ((PointerType) globalPoint.type).objType;
        LLVMType downType = arrayType.getOriginType();
        addValue(new LocalVar(downType, false));
    }*/

    @Override
    public String toString() {
        /*
        if (isFromGlobalArray) {
            //  数组指针所指的元素（数组)的根本成员类型
            ArrayType varPointToT = (ArrayType) type;
            LLVMType targetT = (varPointToT).getOriginType();
            StringBuilder where = new StringBuilder("getelementptr inbounds (");
            // 数组指针所指的元素的类型
            where.append(varPointToT).append(", ");
            where.append(paras.get(0).type).append(" ").append(paras.get(0).toString()).append(", i32 0, ");
            for (int i = 1; i < paras.size() - 1; i++) {
                where.append(paras.get(i).type).append(" ").append(paras.get(i).toString());
                if (i != paras.size() - 2) {
                    where.append(",");
                }
            }
            where.append(")");
            return getAns().toString() + " = load " + targetT + " , " + targetT + "*" + where;

        } else {*/
        String from = paras.get(0).type +
                " " + paras.get(0).toString();
        return getAns().toString() + " = load " + type + ", " + from;
        //}
    }
}
