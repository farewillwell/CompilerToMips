package mid_end.llvm_ir;

import back_end.Mips.AsmInstrs.BlockSignAsm;
import back_end.Mips.MipsBuilder;
import back_end.Mips.MipsSymbol;
import mid_end.llvm_ir.Instrs.ReturnInstr;
import mid_end.llvm_ir.type.BaseType;

import java.util.ArrayList;

public class Function extends User {

    public Function(BaseType type, String name) {
        super(type);
        basicBlocks = new ArrayList<>();
        params = new ArrayList<>();
        this.name = name;
    }

    public final ArrayList<Param> params;

    public final ArrayList<BasicBlock> basicBlocks;

    public final String name;

    public void addPara(Param param) {
        this.params.add(param);
    }

    public void addBasicBlock(BasicBlock block) {
        basicBlocks.add(block);
    }

    // bug :  要是其中没有 return的话，就会导致出错

    public void ensureReturnExist() {
        if (basicBlocks.size() == 0) {
            addBasicBlock(new BasicBlock());
        }
        BasicBlock last = basicBlocks.get(basicBlocks.size() - 1);
        if (last.lastInstrNotRet()) {
            if (type == BaseType.Void) {
                last.addInstr(new ReturnInstr());
            } else {
                last.addInstr(new ReturnInstr(new Constant(0)));
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("define dso_local ");
        stringBuilder.append(type.toString());
        stringBuilder.append(" ");
        stringBuilder.append("@F_").append(name);
        stringBuilder.append(" (");
        for (int i = 0; i < params.size(); i++) {
            stringBuilder.append(params.get(i).type);
            stringBuilder.append(" ");
            stringBuilder.append(params.get(i));
            if (i < params.size() - 1) {
                stringBuilder.append(",");
            }
        }
        stringBuilder.append("){\n");
        for (BasicBlock block : basicBlocks) {
            stringBuilder.append(block);
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    @Override
    public void genMipsCode() {
        new BlockSignAsm(name);
        MipsBuilder.MB.enterNewFunc();
        // para实际上已经存好了,无论如何都是从sp开始的，只需要在这里建上符号表就可以了
        // | a0,a1,a2,a3,a4......|
        // sp ----- 低地址,和数组存起来是相反的。虽然已经在mips实现里面存好了，
        // 但是实际上在这个符号表里面是不知道的，因此要当场alloc
        for (Param param : params) {
            int offset = MipsBuilder.MB.allocOnStack(4);
            MipsSymbol mipsSymbol = new MipsSymbol(param, offset);
            MipsBuilder.MB.addVarSymbol(mipsSymbol);
        }
        for (BasicBlock block : basicBlocks) {
            block.genMipsCode();
        }
        MipsBuilder.MB.exitFunc();
    }
}
