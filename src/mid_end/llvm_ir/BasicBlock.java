package mid_end.llvm_ir;

import back_end.Mips.AsmInstrs.BlockSignAsm;
import mid_end.llvm_ir.Instrs.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class BasicBlock extends Value {
    public final ArrayList<Instr> instrList = new ArrayList<>();
    public final String name;
    public final String nameInMips;
    public final ArrayList<BasicBlock> prev = new ArrayList<>();
    public final ArrayList<BasicBlock> next = new ArrayList<>();
    // dominates:谁支配了该基本块?
    public final HashSet<BasicBlock> whoDomMe = new HashSet<>();
    // 这个基本块支配了谁?
    public final HashSet<BasicBlock> meDomWho = new HashSet<>();
    // 谁直接支配的他？具有唯一性
    public BasicBlock immDomer = null;
    // 他将谁直接支配了
    public HashSet<BasicBlock> beImmDom = new HashSet<>();
    // 支配边界:节点 n 的支配边界是 CFG 中刚好不被 n 支配到的节点集合
    public HashSet<BasicBlock> df = new HashSet<>();

    public BasicBlock() {
        super();
        this.name = IRBuilder.IB.getBasicBlockName();
        this.nameInMips = IRBuilder.IB.getNowFuncName() + name;
    }

    public void addInstr(Instr instr) {
        instrList.add(instr);
    }

    public boolean lastInstrNotRet() {
        return instrList.size() == 0 || !(instrList.get(instrList.size() - 1) instanceof ReturnInstr);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(name).append(":\n");
        for (Instr instr : instrList) {
            stringBuilder.append("  ").append(instr.toString()).append("\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public void genMipsCode() {
        new BlockSignAsm(nameInMips);
        for (Instr instr : instrList) {
            instr.genMipsCode();
        }
    }

    // 清空所有不可到达的指令，比如在branch后的，jump后的和ret后的
    public void cleanInstrAfterOut() {
        int mark = instrList.size();
        for (int i = 0; i < instrList.size(); i++) {
            Instr instr = instrList.get(i);
            if (instr instanceof BranchInstr || instr instanceof JumpInstr || instr instanceof ReturnInstr) {
                mark = i;
                break;
            }
        }
        // 0-mark共mark+1个指令
        while (instrList.size() > mark + 1) {
            instrList.remove(mark + 1);
        }
    }

    public void refillFlowChart() {
        Instr instr = instrList.get(instrList.size() - 1);
        if (instr instanceof JumpInstr) {
            BasicBlock after = ((JumpInstr) instr).getTargetBlock();
            next.add(after);
            after.prev.add(this);
        } else if (instr instanceof BranchInstr) {
            BasicBlock thenBlock = ((BranchInstr) instr).getThenBlock();
            BasicBlock elseBlock = ((BranchInstr) instr).getElseBlock();
            next.add(thenBlock);
            next.add(elseBlock);
            thenBlock.prev.add(this);
            elseBlock.prev.add(this);
        }
    }

    public void printContainBlocks(HashSet<BasicBlock> blocks) {
        System.out.print(this.name + ": who dom me ?{");
        for (BasicBlock block : blocks) {
            System.out.print(block.name + " ");
        }
        System.out.println("}");
    }

    public void printMeDomWho() {
        System.out.print(this.name + ": me dom who ?{");
        for (BasicBlock block : meDomWho) {
            System.out.print(block.name + " ");
        }
        System.out.println("}");
    }

    public void printImmDomMess() {
        System.out.println(this.name + " 's domer : " + (immDomer == null ? "/" : immDomer.name));
        System.out.print(this.name + " 's domed : ");
        for (BasicBlock block : beImmDom) {
            System.out.print(block.name + " ");
        }
        System.out.println();
    }

    public void printDF() {
        System.out.print(this.name + " 's df :");
        for (BasicBlock block : df) {
            System.out.print(block.name + " ");
        }
        System.out.println();
    }

    public boolean queryWhoDomMe(ArrayList<BasicBlock> allNodes) {
        // 由于是求所有集合的交集，因此直接先将全集放进来即可
        HashSet<BasicBlock> hashSet = new HashSet<>(allNodes);
        // 如果一个集合前驱也没有？那么自然是空的了,不能保留!!!
        for (BasicBlock block : prev) {
            hashSet.retainAll(block.whoDomMe);
        }
        if (prev.size() == 0) {
            hashSet.clear();
        }
        hashSet.add(this);
        printContainBlocks(hashSet);
        if (hashSet.size() == whoDomMe.size() && hashSet.containsAll(whoDomMe)) {
            return true;
        } else {
            whoDomMe.clear();
            whoDomMe.addAll(hashSet);
            return false;
        }
    }

    public void refillMeDomWho() {
        for (BasicBlock block : whoDomMe) {
            block.meDomWho.add(this);
        }
    }

    public boolean strictDom(BasicBlock block) {
        return meDomWho.contains(block) && block != this;
    }

    // 直接支配？就是最近的那一个
    // 逆向思维，不去找谁直接支配他，而找他直接支配谁
    // 严格支配n，且不严格支配任何严格支配 n 的节点的节点，我们称其为n的直接支配者
    // 对于本节点就是，对于一个我严格支配的节点A，如果不存在我严格支配的其他一个节点B，B还严格支配A，那么我就直接支配A
    public void queryImmDomer() {
        // 单独开一个，方便遍历节点
        HashSet<BasicBlock> set = new HashSet<>(meDomWho);
        set.remove(this);
        for (BasicBlock blockA : meDomWho) {
            // 如果是严格支配A
            if (blockA != this) {
                // 当前没有找到B
                boolean notContainBDomA = true;
                for (BasicBlock blockB : set) {
                    if (blockB.strictDom(blockA)) {
                        notContainBDomA = false;
                        break;
                    }
                }
                if (notContainBDomA) {
                    blockA.immDomer = this;
                    this.beImmDom.add(blockA);
                }
            }
        }
    }

    // 获取支配边界
    // entry的前驱怎么认定? 注意是前驱而不是immDomer,因此无需考虑该问题
    public void queryDf() {
        // a - > b
        // a is this
        for (BasicBlock b : next) {
            BasicBlock x = this;
            while (x != null && !x.strictDom(b)) {
                x.df.add(b);
                x = x.immDomer;
            }
        }
    }

    public Instr lastInstr() {
        return instrList.get(instrList.size() - 1);
    }

    public void removePhi() {
        Iterator<Instr> iterator = instrList.iterator();
        while (iterator.hasNext()) {
            Instr instr = iterator.next();
            if (instr instanceof PhiInstr) {
                instr.isDeleted = true;
                iterator.remove();
            }
        }
    }

    public boolean hasPhi() {
        return instrList.get(0) instanceof PhiInstr;
    }

    public void insertAtLast(Instr instr) {
        // add index 可以达到插入的功效,index是之后需要这个指令在的位置
        int index = instrList.size() - 1;
        instrList.add(index, instr);
    }

    public void insertBlock(BasicBlock origin, BasicBlock inserter) {
        next.remove(origin);
        next.add(inserter);
        origin.prev.remove(this);
        origin.prev.add(inserter);
        inserter.prev.add(this);
        inserter.next.add(origin);
    }

    public void constBranchTpJump() {
        Instr i = lastInstr();
        if (i instanceof BranchInstr && ((BranchInstr) i).condConst()) {
            BranchInstr bi = (BranchInstr) i;
            instrList.set(instrList.size() - 1, bi.makeEqualJump());
            // 修改 变量关系.支配关系还要改吗?
            //TODO 这里支配关系没有被修改!
            BasicBlock abandon = bi.abandonTarget();
            // 删除下一个块的前驱
            abandon.prev.remove(this);
            // 删除本块的后继
            this.next.remove(abandon);
        }
    }

    // --------------------------活跃变量分析,防止block块内容过多,需要进行疏解-----------------------------------
    public final HashSet<Value> def = new HashSet<>();
    public final HashSet<Value> use = new HashSet<>();
    public final HashSet<Value> in = new HashSet<>();
    public final HashSet<Value> out = new HashSet<>();
}
