package back_end.Mips;

import back_end.Mips.AsmInstrs.*;
import back_end.Mips.MipsHead.Header;
import mid_end.llvm_ir.Value;

import java.util.Stack;

public class MipsBuilder {
    // 结构:.data 开全局，text头初始化，包括main函数在内的所有函数都当作标签
    // 开始的时候jal main ,j end , end标签放最后
    public static final MipsBuilder MB = new MipsBuilder();
    private final FinalAsm finalAsm;

    public MipsBuilder() {
        finalAsm = new FinalAsm();
        managerStack = new Stack<>();
    }

    public void addHead(Header header) {
        finalAsm.addHead(header);
    }

    public void addInstr(AsmInstr asmInstr) {
        //System.out.println(asmInstr);
        finalAsm.addInstr(asmInstr);
    }

    public void headerFinish() {
        new AnnotationAsm("enter main");
        new JumpAsm("jal", "main");
        new AnnotationAsm("leave main");
        new JumpAsm("j", "END");
    }
    // 定义了所有全局变量之类的东西，加上固定的一段跳转程序.

    public void textFinish() {
        new BlockSignAsm("END");
        new LiAsm(Syscall.EXIT, Register.V0);
        new Syscall();
    }

    // 程序段结束，在末尾加一个END,方便跳转到结束
    public String mips() {
        return finalAsm.toString();
    }

    private final Stack<VarManager> managerStack;

    public void enterNewFunc() {
        managerStack.push(new VarManager());
    }

    public void exitFunc() {
        managerStack.pop();
    }

    public boolean hasUsableReg() {
        return managerStack.peek().hasUsableReg();
    }

    public int allocOnStack(int byteSize) {
        return managerStack.peek().allocOnStack(byteSize);
    }

    public Register allocOnReg() {
        return managerStack.peek().allocOnReg();
    }

    public boolean valueInReg(Value value) {
        return managerStack.peek().valueInReg(value);
    }

    public int queryOffset(Value value) {
        return managerStack.peek().queryOffset(value);
    }

    public Register queryReg(Value value) {
        return managerStack.peek().queryReg(value);
    }

    public void addVarSymbol(MipsSymbol varSymbol) {
        managerStack.peek().addLocalSymbol(varSymbol);
    }

    public int getVir() {
        return managerStack.peek().getVir();
    }
}
