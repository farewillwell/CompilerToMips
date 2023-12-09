package back_end.Mips;

import back_end.Mips.AsmInstrs.*;
import back_end.Mips.MipsHead.Header;
import mid_end.llvm_ir.Function;
import mid_end.llvm_ir.Value;

import java.util.HashMap;
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

    public void enterNewFunc(Function function) {
        managerStack.push(function.varManager);
    }

    public void exitFunc() {
        managerStack.pop();
    }


    public int allocOnStack(int byteSize) {
        return managerStack.peek().allocOnStack(byteSize);
    }

    public void backCur(int cur) {
        managerStack.peek().setVir(cur);
    }


    public int queryOffset(Value value) {
        return managerStack.peek().queryOffset(value);
    }

    public Register queryReg(Value value) {
        return managerStack.peek().varSReg.get(value);
    }

    public boolean hasRegFor(Value value) {
        return managerStack.peek().varSReg.containsKey(value);
    }

    public boolean nowInReg(Value value) {
        return managerStack.peek().memMap.get(value).register != null;
    }

    public void storeInReg(Value value, Register register) {
        HashMap<Value, MipsSymbol> map = managerStack.peek().memMap;
        for (Value valueX : map.keySet()) {
            // 将原本的那个换出,并存到它的内存中
            if (map.get(valueX).register == register) {
                //int offset = MB.queryOffset(valueX);
                //new MemAsm(MemAsm.SW, register, Register.SP, offset);
                map.get(valueX).register = null;
            }
        }
        // 先换出再放入
        map.get(value).register = register;

    }

    public void addVarSymbol(MipsSymbol varSymbol) {
        managerStack.peek().addLocalSymbol(varSymbol);
    }

    public int getVir() {
        return managerStack.peek().getVir();
    }

    public void regStoreBack() {
        managerStack.peek().regBackToMem();
    }

    public void memStoreBack() {
        managerStack.peek().memBackToReg();
    }
}
