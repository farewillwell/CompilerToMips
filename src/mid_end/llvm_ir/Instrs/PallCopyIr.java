package mid_end.llvm_ir.Instrs;

import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.Value;

public class PallCopyIr extends Instr {
    // 理论上这个指令不存在任何ans,也不会出现在value约束里面
    public PallCopyIr(Value target, Value moveIn) {
        addValue(target);
        addValue(moveIn);
    }

    @Override
    public String toString() {
        return "pcopy " + getTarget() + " <- " + getMoveIn();
    }

    public Value getTarget() {
        return paras.get(0);
    }

    public Value getMoveIn() {
        return paras.get(1);
    }

    public void setMoveIn(Value moveIn) {
        paras.set(1, moveIn);
    }
}
