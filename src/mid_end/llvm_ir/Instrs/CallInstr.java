package mid_end.llvm_ir.Instrs;

import back_end.Mips.AsmInstrs.*;
import back_end.Mips.MipsBuilder;
import back_end.Mips.MipsSymbol;
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
        addValue(new LocalVar(function.type, false));
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
        for (int i = 0; i < paras.size() - 1; i++) {
            stringBuilder.append(paras.get(i).type.toString());
            stringBuilder.append(" ");
            stringBuilder.append(paras.get(i).toString());
            if (i != paras.size() - 2) {
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
        // 新开一个地方存栈指针的值
        // 当前版本什么寄存器都不需要存，因为我只用了寄存器做中间值
        /*TODO 存储用寄存器存的中间变量*/
        // 预存 sp和ra
        //new AnnotationAsm("这个函数的返回地址ra");
        int raOff = MipsBuilder.MB.allocOnStack(4);
        new MemAsm(MemAsm.SW, Register.RA, Register.SP, raOff);
        // 把需要的参数移动到a0-a3中，要是更多的参数呢？是存到现在的函数栈还是新函数的函数栈？
        // 这里暂时先不管将参数移动到a0-a3的事情了，直接把参数逐个存到新函数的栈区
        // 但是，如果存到新的函数栈的话，我们在移动栈指针的时候该把这些数据都存在哪里呢？
        // 这里需要的是，在移动栈指针的时候，不一次性移动到未赋值的头，而是要移动到下一个函数的参数开始前
        int nowCur = MipsBuilder.MB.getVir();
        for (int i = 0; i < paras.size() - 1; i++) {
            //new AnnotationAsm("存即将跳转到的函数的参数");
            if (paras.get(i) instanceof Constant) {
                new LiAsm(((Constant) paras.get(i)).getValue(), Register.T0);
            } else if (paras.get(i) instanceof LocalVar) {
                int offset = MipsBuilder.MB.queryOffset(paras.get(i));
                new MemAsm(MemAsm.LW, Register.T0, Register.SP, offset);
            } else {
                new MemAsm(MemAsm.LW, Register.T0, ((GlobalVar) paras.get(i)).name, 0);
            }
            int paraOff = MipsBuilder.MB.allocOnStack(4);
            // 所有参数都必然是i32
            new MemAsm(MemAsm.SW, Register.T0, Register.SP, paraOff);
            // 为什么不存到符号表里面？因为之后的符号表都是下一个函数的了，显然不在当前的符号里面
        }
        // 开始换栈
        // 存好了之后，开始将sp放到下一个函数的栈底，也就是当前看到的参数的起始部分
        //new AnnotationAsm("移动栈指针到下一个函数的栈底");
        new AluR2IAsm(AluR2IAsm.ADDI, Register.SP, Register.SP, nowCur);
        //new AnnotationAsm("跳转到函数中:" + function.name);
        new JumpAsm(JumpAsm.JAL, function.name);
        // 回退的时候应该回退的数值是多少?
        // 如果进到的函数不去其他函数的话，那么其sp值显然就是原本的那个底，因此直接回退即可.
        // 要是跳了的话，必然有个尽头，这个尽头不会往别的地方跳，那么就可以在退出的时候原路返回.
        new AluR2IAsm(AluR2IAsm.ADDI, Register.SP, Register.SP, -nowCur);
        new MemAsm(MemAsm.LW, Register.RA, Register.SP, raOff);
        if (function.type == BaseType.I32) {
            //new AnnotationAsm("获取返回值");
            int off = MipsBuilder.MB.allocOnStack(4);
            MipsSymbol mipsSymbol = new MipsSymbol(getAns(), off);
            MipsBuilder.MB.addVarSymbol(mipsSymbol);
            new MemAsm(MemAsm.SW, Register.V0, Register.SP, off);
        }
    }
}
