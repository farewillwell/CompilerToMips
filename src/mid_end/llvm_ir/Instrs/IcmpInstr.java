package mid_end.llvm_ir.Instrs;

import back_end.Mips.AsmInstrs.CmpAsm;
import back_end.Mips.AsmInstrs.LiAsm;
import back_end.Mips.AsmInstrs.MemAsm;
import back_end.Mips.MipsBuilder;
import back_end.Mips.MipsSymbol;
import back_end.Mips.Register;
import front_end.AST.TokenNode;
import front_end.RW;
import mid_end.llvm_ir.*;
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
            throw new RuntimeException("compare not same type :" + para1.type + "--" + para2.type);
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

    public String llOpToMipsOp() {
        return opcode.length() == 2 ? "s" + opcode : opcode;
    }

    @Override
    public void genMipsCode() {
        super.genMipsCode();
        Value p0 = paras.get(0);
        Value p1 = paras.get(1);
        int newOffset = MipsBuilder.MB.allocOnStack(getAns().type.getSize());
        MipsSymbol mipsSymbol = new MipsSymbol(getAns(), newOffset);
        MipsBuilder.MB.addVarSymbol(mipsSymbol);
        if (p0 instanceof Constant) {
            new LiAsm(((Constant) p0).getValue(), Register.T0);
        } else if (p0 instanceof LocalVar) {
            int offset = MipsBuilder.MB.queryOffset(p0);
            new MemAsm(MemAsm.LW, Register.T0, Register.SP, offset);
        } else {
            new MemAsm(MemAsm.LW, Register.T0, ((GlobalVar) p0).name, 0);
        }
        if (p1 instanceof Constant) {
            new LiAsm(((Constant) p1).getValue(), Register.T1);
        } else if (p1 instanceof LocalVar) {
            int offset = MipsBuilder.MB.queryOffset(p1);
            new MemAsm(MemAsm.LW, Register.T1, Register.SP, offset);
        } else {
            new MemAsm(MemAsm.LW, Register.T1, ((GlobalVar) p1).name, 0);
        }
        new CmpAsm(llOpToMipsOp(), Register.T2, Register.T0, Register.T1);
        // 注意,这个得到的值需要存起来,因为你根本不知道比较完的值会不会拿去做计算，而不是用到br去跳转
        int offset = MipsBuilder.MB.allocOnStack(4);
        MipsBuilder.MB.addVarSymbol(new MipsSymbol(getAns(), offset));
        new MemAsm(MemAsm.SW, Register.T2, Register.SP, offset);
    }
}
