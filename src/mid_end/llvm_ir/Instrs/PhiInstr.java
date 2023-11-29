package mid_end.llvm_ir.Instrs;

import mid_end.llvm_ir.BasicBlock;
import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.LocalVar;
import mid_end.llvm_ir.Value;
import mid_end.llvm_ir.type.BaseType;

import java.util.ArrayList;

public class PhiInstr extends Instr {
    public final ArrayList<BasicBlock> from = new ArrayList<>();
    public final Value tieValue;
    // tieValue 就是它绑定的变量指针名
    // 在重命名的时候，把getAns压到tieValue对应的栈里

    public PhiInstr(Value value) {
        super();
        tieValue = value;
        setAns(new LocalVar(BaseType.I32, false));
    }

    public void refill(Value reachValue, BasicBlock fromBlock) {
        addValue(reachValue);
        from.add(fromBlock);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getAns()).append(" = phi i32 ");
        for (int i = 0; i < paras.size(); i++) {
            sb.append("[");
            sb.append(paras.get(i));
            sb.append(",%");
            sb.append(from.get(i).name);
            sb.append("]");
            if (i != paras.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
