package mid_end.llvm_ir;

import back_end.Mips.AsmInstrs.BlockSignAsm;
import back_end.Mips.AsmInstrs.MemAsm;
import back_end.Mips.AsmInstrs.MoveAsm;
import back_end.Mips.MipsBuilder;
import back_end.Mips.MipsSymbol;
import back_end.Mips.Register;
import back_end.Mips.VarManager;
import mid_end.llvm_ir.Instrs.ReturnInstr;
import mid_end.llvm_ir.type.BaseType;

import java.util.ArrayList;
import java.util.HashSet;

public class Function extends User {

    public VarManager varManager = new VarManager();

    public Function(BaseType type, String name) {
        super(type);
        basicBlocks = new ArrayList<>();
        funcParams = new ArrayList<>();
        this.name = name;
        this.localCnt = 1;
        this.bbCnt = 0;
    }

    public final ArrayList<Param> funcParams;

    public final ArrayList<BasicBlock> basicBlocks;

    public final String name;

    public int localCnt;

    public int bbCnt;

    public void addPara(Param param) {
        this.funcParams.add(param);
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
        for (int i = 0; i < funcParams.size(); i++) {
            stringBuilder.append(funcParams.get(i).type);
            stringBuilder.append(" ");
            stringBuilder.append(funcParams.get(i));
            if (i < funcParams.size() - 1) {
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
        MipsBuilder.MB.enterNewFunc(this);
        // para实际上已经存好了,无论如何都是从sp开始的，只需要在这里建上符号表就可以了
        // -----------------------参数问题------------------------- //
        // 事实上,我们已经在call的时候将栈底移动到这里的栈底了,所以默认前置条件满足已经设定好各个元素的位置了
        // 同时,已经分配好了寄存器的参数需要在这里装进去
        for (int i = 0; i < funcParams.size(); i++) {
            int offset = MipsBuilder.MB.allocOnStack(4);
            MipsSymbol mipsSymbol = new MipsSymbol(funcParams.get(i), offset);
            MipsBuilder.MB.addVarSymbol(mipsSymbol);
            // 如果已经分配了寄存器,那么就把东西都存到寄存器里面
            if (MipsBuilder.MB.hasRegFor(funcParams.get(i))) {
                Register register = MipsBuilder.MB.queryReg(funcParams.get(i));
                if (i < 3) {
                    new MoveAsm(register, Register.getWithIndex(5 + i));
                } else {
                    new MemAsm(MemAsm.LW, register, Register.SP, offset);
                }
                MipsBuilder.MB.storeInReg(funcParams.get(i), register);
            }
        }
        for (BasicBlock block : basicBlocks) {
            for (Instr instr : block.instrList) {
                // 仅仅分配好地址,装东西的时候需要在具体执行的时候装
                instr.allocSelf();
            }
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
        showNext();
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
        System.out.println("------------show prev------------");
        for (BasicBlock basicBlock : basicBlocks) {
            System.out.print(basicBlock.name + ":");
            for (BasicBlock block : basicBlock.prev) {
                System.out.print(block.name + " ");
            }
            System.out.println("  ");
        }
    }

    private void showNext() {
        System.out.println("-----------show Next --------------");
        for (BasicBlock basicBlock : basicBlocks) {
            System.out.print(basicBlock.name + ":");
            for (BasicBlock block : basicBlock.next) {
                System.out.print(block.name + " ");
            }
            System.out.println("  ");
        }
    }

    // 定理,不与以entry为根的支配树联通的block不可达
    // 这是因为dom为直接支配，因此每个父节点都支配子节点，因此entry支配树上所有节点
    // 若不支配，则不连通，从entry无法到block,也就可以删除了
    // 事实上,考虑到算法需要每个节点都有且只有一个直接支配节点的性质
    // 因此我们应当在开始的时候删除不可达的而不是在dom阶段删除
    public void cleanUnReachableBlock() {
        HashSet<BasicBlock> unReach = new HashSet<>(basicBlocks);
        dfsDomTreeCheckReach(basicBlocks.get(0), unReach);
        basicBlocks.removeIf(unReach::contains);
        // 别忘了删除他们的使用前后节点关系，不然始终藕断丝连!!!
        for (BasicBlock block : basicBlocks) {
            block.next.removeAll(unReach);
            block.prev.removeAll(unReach);
        }
    }

    private void dfsDomTreeCheckReach(BasicBlock entry, HashSet<BasicBlock> unReachable) {
        unReachable.remove(entry);
        for (BasicBlock block : entry.next) {
            if (unReachable.contains(block)) {
                dfsDomTreeCheckReach(block, unReachable);
            }
        }
    }
}
