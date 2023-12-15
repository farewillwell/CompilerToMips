package mid_end.llvm_ir.Instrs;

import back_end.Mips.AsmInstrs.CmpMips;
import back_end.Mips.MipsBuilder;
import back_end.Mips.Register;
import front_end.AST.TokenNode;
import front_end.RW;
import mid_end.llvm_ir.*;
import mid_end.llvm_ir.type.BaseType;

public class IcmpIr extends Instr {
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

    public boolean foldConst() {
        if (paras.get(0) instanceof Constant && paras.get(1) instanceof Constant) {
            int p0 = ((Constant) paras.get(0)).getValue();
            int p1 = ((Constant) paras.get(1)).getValue();
            Constant ans;
            switch (opcode) {
                case EQ: {
                    ans = new Constant(p0 == p1 ? 1 : 0);
                    break;
                }
                case SGT: {
                    ans = new Constant(p0 > p1 ? 1 : 0);
                    break;
                }
                case SGE: {
                    ans = new Constant(p0 >= p1 ? 1 : 0);
                    break;
                }
                case SLT: {
                    ans = new Constant(p0 < p1 ? 1 : 0);
                    break;
                }
                case SLE: {
                    ans = new Constant(p0 <= p1 ? 1 : 0);
                    break;
                }
                case NE: {
                    ans = new Constant(p0 != p1 ? 1 : 0);
                    break;
                }
                default:
                    throw new RuntimeException("wrong op!");
            }
            getAns().userReplaceMeWith(ans);
            // 这样生成出来结果不是i1的,怎么办?可以直接跳!
            return true;
        } else {
            return false;
        }
    }

    private final String opcode;

    public IcmpIr(String op, Value para1, Value para2) {
        if (para1.type != para2.type) {
            throw new RuntimeException("compare not same type :" + para1.type + "--" + para2.type);
        }
        this.opcode = op;
        addValue(para1);
        addValue(para2);
        setAns(new LocalVar(BaseType.I1, false));
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
        Register op0 = Instr.moveValueIntoReg(Register.T0, p0);
        Register op1 = Instr.moveValueIntoReg(Register.T1, p1);
        Register register = targetSRegorNull(getAns());
        if (register != null) {
            new CmpMips(llOpToMipsOp(), register, op0, op1);
            MipsBuilder.MB.storeInReg(getAns(), register);
        }
        // 注意,这个得到的值需要存起来,因为你根本不知道比较完的值会不会拿去做计算，而不是用到br去跳转
        else {
            new CmpMips(llOpToMipsOp(), Register.T2, op0, op1);
            Instr.storeMemFromReg(Register.T2, getAns());
        }
    }

    @Override
    public boolean canBeGVN() {
        return true;
    }

    @Override
    public String gvnCode() {
        return opcode + paras.get(0) + paras.get(1);
    }
}
