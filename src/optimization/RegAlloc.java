package optimization;

import back_end.Mips.Register;
import back_end.Mips.VarManager;
import mid_end.llvm_ir.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class RegAlloc {
    private VarManager manager = null;
    // 可以采取一个循环队列,来选择释放变量,防止连续使用的时候疯狂换入换出
    // 但是这就是后话了

    public void solve(IRModule irModule) {
        for (Function function : irModule.functions) {
            manager = function.varManager;
            alloc(function);
        }
    }

    private void alloc(Function function) {
        // 首先需要分配所有参数的寄存器,因为参数大概率是经常用的
        // 注意,参数除了类型可能是指针之外并没有什么不同
        // 目的是设计,在call的时候把所有用到的寄存器物归原主
        // 比起新开栈存起来好多了
        ArrayList<Param> params = function.funcParams;
        for (int i = 0; i < params.size() && manager.hasUsableReg(); i++) {
            manager.allocOnReg(params.get(i));
        }
        //这里应该仅仅对入口使用,否则会在支配树的dfs中额外多出很多操作
        System.out.println("-------------alloc for " + function.name + " ---------------");
        HashSet<BasicBlock> visited = new HashSet<>();
        blockAlloc(function.basicBlocks.get(0), visited);
    }

    // 考虑到dfs,所有和寄存器有关的东西必须是栈可恢复的
    // 当然,变量分配给哪个寄存器不用做到这一点
    // 万恶之源:这个是在消phi之后完成的
    private void blockAlloc(BasicBlock block, HashSet<BasicBlock> visited) {
        System.out.println("---------block " + block.name + "---------------");
        visited.add(block);
        // 在这里活着但是后继子节点死了的寄存器
        HashSet<Value> dead = new HashSet<>();
        // 在这里最后一次使用,配合上面的可以及时释放寄存器
        HashMap<Value, Instr> lastUse = new HashMap<>();
        // 求出每一个值的最后使用
        for (Instr instr : block.instrList) {
            for (Value value : instr.paras) {
                if ((value instanceof LocalVar || value instanceof Param)) {
                    lastUse.put(value, instr);
                }
            }
        }
        for (Instr instr : block.instrList) {
            // 解放所有死变量的寄存器
            for (Value value : instr.paras) {
                if (lastUse.get(value) == instr && !block.out.contains(value)) {
                    System.out.print("last use " + value);
                    manager.releaseReg(manager.varSReg.get(value));
                    dead.add(value);
                }
            }
            // 如果已经满了的话怎么办?那么我就暂时不分配了,要不然还要换回到内存里面
            // 注意,这里可能存在多次alloc的情况,因为比如会在多个块的move里面有对该value的move
            // 所以需要专门特判解决
            // 问题是,如果在一次定义的时候这里很复杂,没有分配东西,
            // 但是在另一次定义的时候这里很宽松,分配了,那么以后即使是在第一次用的时候也会错误的使用这个寄存器的值了
            // 怎么办?
            // 事实上,在第一次的时候就已经溢出了,我们应该把这个变量设置为不分配全局寄存器的
            if (instr.getAns() != null && !manager.varSReg.containsKey(instr.getAns())) {
                if (manager.hasUsableReg()) {
                    manager.allocOnReg(instr.getAns());
                }
            }
        }
        HashMap<Register, Value> store = new HashMap<>(manager.regUsed);
        // 这里遍历的应该是后驱吧,而不是直接支配的,应该是next
        // 活跃变量分析和支配没关系的
        for (BasicBlock child : block.next) {
            if (!visited.contains(child)) {
                // 解放所有不包含在child in 中的寄存器
                for (Register register : store.keySet()) {
                    if (!child.in.contains(store.get(register))) {
                        System.out.print("child " + child.name);
                        manager.releaseReg(register);
                    }
                }
                blockAlloc(child, visited);
                // 一定要在下一次遍历前把东西都恢复了
                manager.regUsed.clear();
                manager.regUsed.putAll(store);
            }
        }
        // 考虑到这里定义的寄存器不再需要了(因为它支配的块没了,剩下的块由于不确定是否经过这个块,因此必然不会用到,因此我们可以把它定义的删掉)
        // 事实上,这是一个极其错误的决定
        // 例如 b18->b6,b5->b6,这两个人都定义了变量54,b6中要用到54,但是这都不算在b18或者b5的直接支配节点里面
        // 导致出来之后54分配的就被放了
        // 所以不是支配,而是后继
        for (Value value : block.def) {
            System.out.print("def " + value + " :");
            manager.releaseReg(manager.varSReg.get(value));
        }
        // 恢复所有所谓的死变量
        for (Value value : dead) {
            manager.regUsed.put(manager.varSReg.get(value), value);
        }
        System.out.println("-------------" + block.name + "------------end");
    }
}
