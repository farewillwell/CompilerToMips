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

    public void cleanInstrAfterOutForBlock() {
        for (BasicBlock block : basicBlocks) {
            block.cleanInstrAfterOut();
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

    //------------------------------------------------------------------------------------
    public void refillFlowChart() {
        for (BasicBlock block : basicBlocks) {
            block.refillFlowChart();
        }
    }

    public void queryDom() {
        showPrev();
        boolean ok = false;
        int count = 0;
        // out[entry]=v_entry,  out == dominates
        basicBlocks.get(0).whoDomMe.add(basicBlocks.get(0));
        // for (Entry外的每个基本块) out[B] = T ,这里 T = N
        for (int i = 1; i < basicBlocks.size(); i++) {
            basicBlocks.get(i).whoDomMe.addAll(basicBlocks);
        }
        // while 某个out值发生变化
        while (!ok) {
            ok = true;
            System.out.println("pass" + ++count);
            for (int i = 0; i < basicBlocks.size(); i++) {
                if (i == 0) {
                    basicBlocks.get(i).printContainBlocks(basicBlocks.get(i).whoDomMe);
                } // 除了entry外的每个基本块B
                else if (!basicBlocks.get(i).queryWhoDomMe(basicBlocks)) {
                    ok = false;// 这里如果用&&莫名其妙会优化?
                }
            }
        }
        for (BasicBlock block : basicBlocks) {
            block.refillMeDomWho();
        }
        for (BasicBlock block : basicBlocks) {
            block.printMeDomWho();
        }
    }

    public void queryImmDomTree() {
        for (BasicBlock block : basicBlocks) {
            block.queryImmDomer();
            block.printImmDomMess();
        }
    }

    public void queryDf() {
        for (BasicBlock block : basicBlocks) {
            block.queryDf();
        }
        for (BasicBlock block : basicBlocks) {
            block.printDF();
        }
    }

    private void showPrev() {
        for (BasicBlock basicBlock : basicBlocks) {
            System.out.print(basicBlock.name + ":");
            for (BasicBlock block : basicBlock.prev) {
                System.out.print(block.name + " ");
            }
            System.out.println("  ");
        }
    }
}
