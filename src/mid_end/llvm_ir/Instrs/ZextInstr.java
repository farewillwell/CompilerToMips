package mid_end.llvm_ir.Instrs;

import back_end.Mips.MipsBuilder;
import back_end.Mips.MipsSymbol;
import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.LocalVar;
import mid_end.llvm_ir.Value;
import mid_end.llvm_ir.type.BaseType;

public class ZextInstr extends Instr {
    // extend 专门用于扩展,压缩的可以用icmp和0来表示

    public ZextInstr(BaseType targetType, Value beChangedValue) {
        addValue(beChangedValue);
        setAns(new LocalVar(targetType, false));
    }

    @Override
    public String toString() {
        return getAns() + " = zext " + paras.get(0).type + " " + paras.get(0) + " to " + getAns().type;
    }

    @Override
    public void genMipsCode() {
        super.genMipsCode();
        // mips里面没有1位的，只需要把这个指令得到的offset赋值给新的到的value就好了
        int offset = MipsBuilder.MB.queryOffset(paras.get(0));
        MipsSymbol mipsSymbol = new MipsSymbol(getAns(), offset);
        MipsBuilder.MB.addVarSymbol(mipsSymbol);
    }
}
