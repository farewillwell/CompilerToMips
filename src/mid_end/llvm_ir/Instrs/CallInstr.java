package mid_end.llvm_ir.Instrs;

import mid_end.llvm_ir.Function;
import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.LocalVar;
import mid_end.llvm_ir.Value;
import mid_end.llvm_ir.type.BaseType;

import java.util.ArrayList;


public class CallInstr extends Instr {
    private final Function function;


    public CallInstr(Function function, ArrayList<Value> rps) {
        this.function = function;
        for (Value value : rps) {
            addValue(value);
        }
        if (function.type != BaseType.Void) {
            addValue(new LocalVar(function.type, false));
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (function.type != BaseType.Void) {
            stringBuilder.append(getAns());
            stringBuilder.append(" = ");
        }
        stringBuilder.append("call ");
        stringBuilder.append(function.type.toString());
        stringBuilder.append(" ");
        stringBuilder.append(function.name);
        stringBuilder.append(" (");
        for (int i = 0; i < paras.size() - 1; i++) {
            stringBuilder.append(paras.get(i).type.toString());
            stringBuilder.append(" ");
            stringBuilder.append(paras.get(i).toString());
            if (i != paras.size() - 2) {
                stringBuilder.append(" , ");
            }
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
