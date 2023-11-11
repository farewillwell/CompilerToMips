package mid_end.llvm_ir.Instrs;

import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.LocalVar;
import mid_end.llvm_ir.Value;
import mid_end.llvm_ir.type.BaseType;

public class ALUInstr extends Instr {

    //  and  /  or become jump of basic block
    public static final String ADD = "add ";
    public static final String SUB = "sub ";
    public static final String MUL = "mul ";
    public static final String DIV = "sdiv ";
    public static final String SREM = "srem ";
    public static final String AND = "AND";
    public static final String OR = "OR";

    private final String opcode;

    // 可扩展？i32 或者什么？

    public ALUInstr(String op, Value para1, Value para2) {
        this.opcode = op;
        this.addValue(para1);
        this.addValue(para2);
        this.addValue(new LocalVar(BaseType.I32,false));
    }

    @Override
    public String toString() {
        return this.getAns().toString() + " = " + opcode + " "+ getAns().type.toString()+" "+
                paras.get(0).toString() +", "+ paras.get(1).toString();
    }
}
