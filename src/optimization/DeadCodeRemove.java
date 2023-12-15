package optimization;

import mid_end.llvm_ir.*;
import mid_end.llvm_ir.Instrs.BranchIr;
import mid_end.llvm_ir.Instrs.CallIr;
import mid_end.llvm_ir.Instrs.IO.IOInstr;
import mid_end.llvm_ir.Instrs.JumpIr;
import mid_end.llvm_ir.Instrs.MoveIr;

import java.util.HashSet;
import java.util.Iterator;

public class DeadCodeRemove {
    public void solve(IRModule irModule) {
        for (Function function : irModule.functions) {
            onlyJumpBlockRemove(function);
            // 在常量扩散中，对于语句的分支会减少很多，导致程序的逻辑结构变化
            // 有些能到的块，可能就到不了了，即使原来能到
            // 如果已经确定到不了的块，但是其他instr什么的用了其中的变量,怎么办?
            // 这个凭空多出来的变量,也有可能对以后的分析造成影响
            // 尝试:把发现用了没有定义的全删了!!!
            // 如果怕有错把剩下这俩ban了就行
            function.cleanUnReachableBlock();
            removeNoDefineUser(function);
            boolean ans = true;
            while (ans) {
                ans = removeUnUsedInstr(function);
            }
        }
    }

    // 通过语句,先扫描全部块，找到所有定义的,这样可以把使用不被定义的变量的move删除,这里我们默认只删move
    private void removeNoDefineUser(Function function) {
        // 注意，参数当然也是必须定义的!!!
        HashSet<Value> definedValue = new HashSet<>(function.funcParams);
        for (BasicBlock block : function.basicBlocks) {
            for (Instr instr : block.instrList) {
                if (instr.getAns() != null) {
                    definedValue.add(instr.getAns());
                }
            }
        }
        for (BasicBlock block : function.basicBlocks) {
            block.instrList.removeIf(instr -> instr instanceof MoveIr && ((MoveIr) instr).useUndefine(definedValue));
        }
    }

    private void onlyJumpBlockRemove(Function function) {
        HashSet<BasicBlock> toRemove = new HashSet<>();
        BasicBlock entry = function.basicBlocks.get(0);
        // 这个的实现者似乎还是块本身比较好，对所有前驱进行验证修改，都指到后驱上,把自己的前驱都指到自己的后驱上
        // 确定需要remove的？当然是自己了
        dfsRemoveNextBlock(entry, toRemove, new HashSet<>());
        function.basicBlocks.removeAll(toRemove);
    }

    private void dfsRemoveNextBlock(BasicBlock entry, HashSet<BasicBlock> toRemove, HashSet<BasicBlock> vis) {
        // 后序遍历，先删除当前的子节点,但是可能会爆删除错误.所以我们需要新开一个集合把这个东西先存起来
        // 爆栈怎么办?可能会循环引用?很简单，和之前防止这个的方法一样，加一个遍历集合即可
        vis.add(entry);
        HashSet<BasicBlock> sons = new HashSet<>(entry.next);
        for (BasicBlock son : sons) {
            if (!vis.contains(son)) {
                dfsRemoveNextBlock(son, toRemove, vis);
            }
        }
        // 原则，带有判断逻辑的branch都不删掉，要删只删掉jump的块
        if (entry.instrList.size() == 1 && (entry.instrList.get(0) instanceof JumpIr)) {
            toRemove.add(entry);
            JumpIr jumpIr = (JumpIr) entry.instrList.get(0);
            BasicBlock target = jumpIr.getTargetBlock();
            for (BasicBlock father : entry.prev) {
                Instr instr = father.instrList.get(father.instrList.size() - 1);
                if (instr instanceof BranchIr) {
                    if (((BranchIr) instr).getThenBlock() == entry) {
                        ((BranchIr) instr).changeThen(target);
                    } else {
                        ((BranchIr) instr).changeElse(target);
                    }
                } else {
                    ((JumpIr) instr).setDstBlock(target);
                }
                // -----------结束后，不要忘了把前驱后驱改变了-------------------
                father.next.remove(entry);
                father.next.add(target);
                target.prev.remove(entry);
                target.prev.add(father);
            }
            entry.prev.clear();
            entry.next.clear();
            // 为彻底删掉做准备
        }
    }

    private boolean removeUnUsedInstr(Function function) {
        boolean ans = false;
        for (BasicBlock block : function.basicBlocks) {
            Iterator<Instr> iterator = block.instrList.iterator();
            while (iterator.hasNext()) {
                Instr instr = iterator.next();
                // 函数的过程和IOI的过程都是必须的,可以试试函数展开
                if (instr.getAns() != null && !(instr instanceof CallIr || instr instanceof IOInstr)) {
                    if (instr.getAns().userEmpty()) {
                        iterator.remove();
                        instr.isDeleted = true;
                        ans = true;
                    }
                }
            }
        }
        return ans;
    }

}
