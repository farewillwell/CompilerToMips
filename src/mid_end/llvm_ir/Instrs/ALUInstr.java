package mid_end.llvm_ir.Instrs;

import back_end.Mips.AsmInstrs.*;
import back_end.Mips.MipsBuilder;
import back_end.Mips.MipsSymbol;
import back_end.Mips.Register;
import mid_end.llvm_ir.*;
import mid_end.llvm_ir.type.BaseType;

import java.util.ArrayList;

public class ALUInstr extends Instr {

    //  and  /  or become jump of basic block
    public static final String ADD = "add";
    public static final String SUB = "sub";
    public static final String MUL = "mul";
    public static final String DIV = "sdiv";
    public static final String SREM = "srem";

    private final String opcode;

    // 可扩展？i32 或者什么？

    public ALUInstr(String op, Value para1, Value para2) {
        this.opcode = op;
        this.addValue(para1);
        this.addValue(para2);
        this.addValue(new LocalVar(BaseType.I32, false));
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

    /*TODO 除法优化*/

    @Override
    public void genMipsCode() {
        super.genMipsCode();
        Value p0 = paras.get(0);
        Value p1 = paras.get(1);
        int newOffset = MipsBuilder.MB.allocOnStack(getAns().type.getSize());
        MipsSymbol mipsSymbol = new MipsSymbol(getAns(), newOffset);
        MipsBuilder.MB.addVarSymbol(mipsSymbol);
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
                if (var instanceof LocalVar) {
                    int offset = MipsBuilder.MB.queryOffset(var);
                    new MemAsm(MemAsm.LW, Register.T0, Register.SP, offset);
                } else {
                    new MemAsm(MemAsm.LW, Register.T0, ((GlobalVar) var).nameInMips(), 0);
                }
                if (shifts.size() == 1) {
                    new AluR2IAsm(AluR2IAsm.SLL, Register.T2, Register.T0, shifts.get(0));
                    if (isNegative) {
                        new AluR2RAsm(AluR2RAsm.SUBU, Register.T2, Register.ZERO, Register.T2);
                    }
                    new MemAsm(MemAsm.SW, Register.T2, Register.SP, newOffset);
                    return;
                }
                // 清0 T2
                new AluR2RAsm(AluR2RAsm.ADDU, Register.T2, Register.ZERO, Register.ZERO);
                for (Integer integer : shifts) {
                    if (integer != 0) {
                        new AluR2IAsm(AluR2IAsm.SLL, Register.T1, Register.T0, integer);
                        new AluR2RAsm(AluR2RAsm.ADDU, Register.T2, Register.T1, Register.T2);
                    } else {
                        new AluR2RAsm(AluR2RAsm.ADDU, Register.T2, Register.T0, Register.T2);
                    }
                }
                if (isNegative) {
                    new AluR2RAsm(AluR2RAsm.SUBU, Register.T2, Register.ZERO, Register.T2);
                }
                new MemAsm(MemAsm.SW, Register.T2, Register.SP, newOffset);
                return;
            }
        }
        //-----------------------------------------------------//
        if (p0 instanceof Constant) {
            new LiAsm(((Constant) p0).getValue(), Register.T0);
        } else if (p0 instanceof LocalVar) {
            int offset = MipsBuilder.MB.queryOffset(p0);
            new MemAsm(MemAsm.LW, Register.T0, Register.SP, offset);
        } else {
            new MemAsm(MemAsm.LW, Register.T0, ((GlobalVar) p0).nameInMips(), 0);
        }
        if (p1 instanceof Constant) {
            new LiAsm(((Constant) p1).getValue(), Register.T1);
        } else if (p1 instanceof LocalVar) {
            int offset = MipsBuilder.MB.queryOffset(p1);
            new MemAsm(MemAsm.LW, Register.T1, Register.SP, offset);
        } else {
            new MemAsm(MemAsm.LW, Register.T1, ((GlobalVar) p1).nameInMips(), 0);
        }
        if (opcode.equals(ADD)) {
            new AluR2RAsm(AluR2RAsm.ADDU, Register.T2, Register.T0, Register.T1);
        } else if (opcode.equals(SUB)) {
            new AluR2RAsm(AluR2RAsm.SUBU, Register.T2, Register.T0, Register.T1);
        } else {
            if (opcode.equals(MUL)) {
                new MulDivAsm(MulDivAsm.MUL, Register.T0, Register.T1);
                new HiLoGetterAsm(HiLoGetterAsm.MFLO, Register.T2);
            } else if (opcode.equals(DIV)) {
                new MulDivAsm(MulDivAsm.DIV, Register.T0, Register.T1);
                new HiLoGetterAsm(HiLoGetterAsm.MFLO, Register.T2);
            } else {
                new MulDivAsm(MulDivAsm.DIV, Register.T0, Register.T1);
                new HiLoGetterAsm(HiLoGetterAsm.MFHI, Register.T2);
            }
        }
        new MemAsm(MemAsm.SW, Register.T2, Register.SP, newOffset);
    }
}
