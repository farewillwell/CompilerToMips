package mid_end.llvm_ir.Instrs;

import mid_end.llvm_ir.GlobalVar;
import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.LocalVar;
import mid_end.llvm_ir.Value;
import mid_end.llvm_ir.type.ArrayType;
import mid_end.llvm_ir.type.LLVMType;
import mid_end.llvm_ir.type.PointerType;

import java.util.ArrayList;

public class StoreInstr extends Instr {

    // private boolean isToGlobalArray = false;

    /*TODO 数组的赋值*/
    // store value 是要被存的值
    public StoreInstr(Value storeValue, Value inTo) {
        //isToGlobalArray = false;
        addValue(storeValue);
        addValue(inTo);
    }
    /*
    public StoreInstr(Value storeValue, ArrayList<Value> offsets, GlobalVar globalPoint) {
        isToGlobalArray = true;
        addValue(storeValue);
        for (Value value : offsets) {
            addValue(value);
        }
        addValue(globalPoint);
    }*/

    @Override
    public String toString() {
        /*
        if (isToGlobalArray) {
            //  数组指针所指的元素（数组)的根本成员类型
            ArrayType varPointToT = (ArrayType) ((PointerType) getAns().type).objType;
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
        return "store " + paras.get(0).type + " " + paras.get(0) +
                " , " + paras.get(1).type + " " + paras.get(1);
        //}
    }

    @Override
    public Value getAns() {
        throw new RuntimeException("get ans of a store instr");
    }
}
