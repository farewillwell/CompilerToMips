package mid_end.llvm_ir.Instrs;

import front_end.AST.TokenNode;
import front_end.RW;
import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.LocalVar;
import mid_end.llvm_ir.Value;
import mid_end.llvm_ir.type.BaseType;

public class IcmpInstr extends Instr {
    // 无论eq还是rel，其实使用的指令都是Icmp , 只不过操作不一样罢了
    public static final String EQ = "eq";
    // equal
    public static final String NE = "ne";
    // not equal
    public static final String SGT = "sgt";
    // greater than
    public static final String SGE = "sge";
    public static final String SLT = "slt";
    public static final String SLE = "sle";

    public static String chooseString(TokenNode tokenNode) {
        RW.TYPE reType = tokenNode.type();
        if (reType == RW.TYPE.EQL) {
            return EQ;
        } else if (reType == RW.TYPE.NEQ) {
            return NE;
        } else if (reType == RW.TYPE.GRE) {
            return SGT;
        } else if (reType == RW.TYPE.GEQ) {
            return SGE;
        } else if (reType == RW.TYPE.LSS) {
            return SLT;
        } else if (reType == RW.TYPE.LEQ) {
            return SLE;
        } else {
            throw new RuntimeException("not a opcode for icmp :" + reType);
        }
    }

    private final String opcode;

    public IcmpInstr(String op, Value para1, Value para2) {
        if (para1.type != para2.type) {
            throw new RuntimeException("compare not same type");
        }
        this.opcode = op;
        addValue(para1);
        addValue(para2);
        addValue(new LocalVar(BaseType.I1, false));
    }

    @Override
    public String toString() {
        Value operand1 = paras.get(0);
        Value operand2 = paras.get(1);
        return getAns() + " = icmp " + opcode + " " + operand1.type + " " + operand1 + ", " + operand2;
    }
}
