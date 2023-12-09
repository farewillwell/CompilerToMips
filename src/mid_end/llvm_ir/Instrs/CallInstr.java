package mid_end.llvm_ir.Instrs;

import back_end.Mips.AsmInstrs.*;
import back_end.Mips.MipsBuilder;
import back_end.Mips.Register;
import mid_end.llvm_ir.*;
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
            setAns(new LocalVar(function.type, false));
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
        stringBuilder.append(" @F_");
        stringBuilder.append(function.name);
        stringBuilder.append(" (");
        for (int i = 0; i < paras.size(); i++) {
            stringBuilder.append(paras.get(i).type.toString());
            stringBuilder.append(" ");
            stringBuilder.append(paras.get(i).toString());
            if (i != paras.size() - 1) {
                stringBuilder.append(" , ");
            }
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    /**
     * 返回值：v0
     * 返回地址 ra
     */
    @Override
    public void genMipsCode() {
        super.genMipsCode();
        // 进入函数流程:
        // 预存 ra
        int raOff = MipsBuilder.MB.allocOnStack(4);
        new MemAsm(MemAsm.SW, Register.RA, Register.SP, raOff);
        // 开始将所有使用到的寄存器归位
        MipsBuilder.MB.regStoreBack();
        // 注意,输出的时候可能会用到a0,所以a0也是不能用的!!!
        // 这里需要的是，在移动栈指针的时候，不一次性移动到未赋值的头，而是要移动到下一个函数的参数开始前
        int nowCur = MipsBuilder.MB.getVir();
        for (int i = 0; i < paras.size(); i++) {
            Register op = Instr.moveValueIntoReg(Register.T0, paras.get(i));
            int paraOff = MipsBuilder.MB.allocOnStack(4);
            if (i < 3) {
                new MoveAsm(Register.getWithIndex(5 + i), op);
            } else {
                new MemAsm(MemAsm.SW, op, Register.SP, paraOff);
            }
        }
        // 在分配完参数之后,需要把cur放回去,否则会导致两个函数的交接有问题
        MipsBuilder.MB.backCur(nowCur);
        // 存好了之后，开始将sp放到下一个函数的栈底，也就是当前看到的参数的起始部分
        new AluR2IAsm(AluR2IAsm.ADDI, Register.SP, Register.SP, nowCur);
        new JumpAsm(JumpAsm.JAL, function.name);
        new AluR2IAsm(AluR2IAsm.ADDI, Register.SP, Register.SP, -nowCur);
        new MemAsm(MemAsm.LW, Register.RA, Register.SP, raOff);
        MipsBuilder.MB.memStoreBack();
        if (function.type == BaseType.I32) {
            Register register = targetSRegorNull(getAns());
            if (register != null) {
                new MoveAsm(register, Register.V0);
                MipsBuilder.MB.storeInReg(getAns(), register);
            } else {
                Instr.storeMemFromReg(Register.V0, getAns());
            }
        }
    }
}
