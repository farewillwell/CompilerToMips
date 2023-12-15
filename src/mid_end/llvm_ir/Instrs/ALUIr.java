package mid_end.llvm_ir.Instrs;

import back_end.Mips.AsmInstrs.*;
import back_end.Mips.MipsBuilder;
import back_end.Mips.Register;
import mid_end.llvm_ir.*;
import mid_end.llvm_ir.type.BaseType;

import java.util.ArrayList;

public class ALUIr extends Instr {

    //  and  /  or become jump of basic block
    public static final String ADD = "add";
    public static final String SUB = "sub";
    public static final String MUL = "mul";
    public static final String DIV = "sdiv";
    public static final String SREM = "srem";

    private final String opcode;

    // 可扩展？i32 或者什么？

    public ALUIr(String op, Value para1, Value para2) {
        this.opcode = op;
        this.addValue(para1);
        this.addValue(para2);
        setAns(new LocalVar(BaseType.I32, false));
    }

    @Override
    public String toString() {
        return this.getAns().toString() + " = " + opcode + " " + getAns().type.toString() + " " +
                paras.get(0).toString() + ", " + paras.get(1).toString();
    }
    /* 除法非常慢，所以尽量不用除法,乘法需要计算下分解的开销
     * */

    // 负数的问题?
    public static ArrayList<Integer> shiftList(int num) {
        // 一次shift的代价是1，一次add的代价也是1，计算其代价为：
        // ans[0]==0? 2*len-2/2*len-1
        // 和5比较: 2*len-1<=5
        // len<=3
        // 注意如果是0的情况,那么会优先判断得到0
        ArrayList<Integer> ans = new ArrayList<>();
        int bit = 0;
        while (num != 0) {
            if (num % 2 == 1) {
                ans.add(bit);
            }
            num /= 2;
            bit++;
        }
        if (ans.size() <= 3) {
            return ans;
        }
        return null;
    }

    public boolean foldConst() {
        if (paras.get(0) instanceof Constant && paras.get(1) instanceof Constant) {
            int p0 = ((Constant) paras.get(0)).getValue();
            int p1 = ((Constant) paras.get(1)).getValue();
            Constant ans;
            switch (opcode) {
                case ADD: {
                    ans = new Constant(p0 + p1);
                    break;
                }
                case SUB: {
                    ans = new Constant(p0 - p1);
                    break;
                }
                case MUL: {
                    ans = new Constant(p0 * p1);
                    break;
                }
                case DIV: {
                    ans = new Constant(p0 / p1);
                    break;
                }
                case SREM: {
                    ans = new Constant(p0 % p1);
                    break;
                }
                default:
                    throw new RuntimeException("wrong op!");
            }
            getAns().userReplaceMeWith(ans);
            return true;
        } else {
            return false;
        }
    }

    /*TODO 除法优化*/

    @Override
    public void genMipsCode() {
        super.genMipsCode();
        Value p0 = paras.get(0);
        Value p1 = paras.get(1);
        Register register = targetSRegorNull(getAns());
        // 如果两边都是常数,已经在前面处理过了，因此不用理会.
        //----------------------------------------------------//
        // 如果乘法有一个为常数
        if (opcode.equals(MUL) && (p0 instanceof Constant || p1 instanceof Constant)) {
            //------------------------------------------------------------------------------
            // 预处理，减少分支
            Value var = p0 instanceof Constant ? p1 : p0;
            int constant = p0 instanceof Constant ?
                    ((Constant) p0).getValue() : ((Constant) p1).getValue();
            boolean isNegative = false;
            //--------------------------------------------------------------------------------
            // 不会出现乘数是0的情况，会在getIr阶段被拦截
            if (constant < 0) {
                constant = -constant;
                isNegative = true;
            }
            ArrayList<Integer> shifts = shiftList(constant);
            if (shifts != null) {
                if (shifts.size() == 0) {
                    throw new RuntimeException();
                }
                Register op = Instr.moveValueIntoReg(Register.T0, var);
                if (shifts.size() == 1) {
                    new AluR2IMips(AluR2IMips.SLL, Register.T2, op, shifts.get(0));
                    if (isNegative) {
                        new AluR2RMips(AluR2RMips.SUBU, Register.T2, Register.ZERO, Register.T2);
                    }
                    if (register != null) {
                        new MoveMips(register, Register.T2);
                        MipsBuilder.MB.storeInReg(getAns(), register);
                    } else {
                        Instr.storeMemFromReg(Register.T2, getAns());
                    }
                    return;
                }
                // 清0 T2
                new AluR2RMips(AluR2RMips.ADDU, Register.T2, Register.ZERO, Register.ZERO);
                for (Integer integer : shifts) {
                    if (integer != 0) {
                        new AluR2IMips(AluR2IMips.SLL, Register.T1, op, integer);
                        new AluR2RMips(AluR2RMips.ADDU, Register.T2, Register.T1, Register.T2);
                    } else {
                        new AluR2RMips(AluR2RMips.ADDU, Register.T2, op, Register.T2);
                    }
                }
                if (isNegative) {
                    new AluR2RMips(AluR2RMips.SUBU, Register.T2, Register.ZERO, Register.T2);
                }
                if (register != null) {
                    new MoveMips(register, Register.T2);
                    MipsBuilder.MB.storeInReg(getAns(), register);
                } else {
                    Instr.storeMemFromReg(Register.T2, getAns());
                }
                return;
            }
        }
        //-----------------------------------------------------//
        Register op0 = Instr.moveValueIntoReg(Register.T0, p0);
        Register op1 = Instr.moveValueIntoReg(Register.T1, p1);
        Register ans = register == null ? Register.T2 : register;
        if (opcode.equals(ADD)) {
            new AluR2RMips(AluR2RMips.ADDU, ans, op0, op1);
        } else if (opcode.equals(SUB)) {
            new AluR2RMips(AluR2RMips.SUBU, ans, op0, op1);
        } else {
            if (opcode.equals(MUL)) {
                new MulDivMips(MulDivMips.MUL, op0, op1);
                new HiLoGetterMips(HiLoGetterMips.MFLO, ans);
            } else if (opcode.equals(DIV)) {
                new MulDivMips(MulDivMips.DIV, op0, op1);
                new HiLoGetterMips(HiLoGetterMips.MFLO, ans);
            } else {
                new MulDivMips(MulDivMips.DIV, op0, op1);
                new HiLoGetterMips(HiLoGetterMips.MFHI, ans);
            }
        }
        if (register == null) {
            Instr.storeMemFromReg(Register.T2, getAns());
        } else {
            MipsBuilder.MB.storeInReg(getAns(), register);
        }
    }

    @Override
    public boolean canBeGVN() {
        return true;
    }

    // 满足交换律,可以进行操作
    @Override
    public String gvnCode() {
        Value p0;
        Value p1;
        // hashCode大的在先
        if (opcode.equals(ADD) || opcode.equals(MUL)) {
            if (paras.get(0).hashCode() > paras.get(1).hashCode()) {
                p0 = paras.get(0);
                p1 = paras.get(1);
            } else {
                p0 = paras.get(1);
                p1 = paras.get(0);
            }
        } else {
            p0 = paras.get(0);
            p1 = paras.get(1);
        }
        return opcode + p0 + p1;
    }
}
