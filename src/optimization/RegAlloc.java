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
    // 冲突问题:例如icmp得到一个值,然后此时还活跃着一个虽然现在第一遍遍历的时候没出现但是
    // 实际上在这里可能会有值的情况.如果出现了这样的冲突?
    // 因此,在对指令分析之前,需要对所有的in进来的变量进行分配指令
    private void blockAlloc(BasicBlock block, HashSet<BasicBlock> visited) {
        System.out.println("---------block " + block.name + "---------------");
        visited.add(block);
        // 在这里活着但是后继子节点死了的寄存器
        HashSet<Value> dead = new HashSet<>();
        // 在这里最后一次使用,配合上面的可以及时释放寄存器
        HashMap<Value, Instr> lastUse = new HashMap<>();
        // 对所有in变量进行操作,将其使用的寄存器换进来,防止和本身的冲突
        for (Value value : block.in) {
            if (!manager.varSReg.containsKey(value)) {
                if (manager.hasUsableReg() && !manager.notHasReg.contains(value)) {
                    manager.allocOnReg(value);
                }
            } else {
                manager.refillAllocReg(value);
            }
        }
        // 对所有out变量,如果已经提前预设定好了的话,那么就把这个寄存器加进去,防止冲突
        for (Value value : block.out) {
            if (manager.varSReg.containsKey(value)) {
                manager.refillAllocReg(value);
            }
        }
        // 当然,还有一个问题,就是如果out变量的寄存器和中间的寄存器冲突了,而out的寄存器已经分配好了
        // 例如,当前的寄存器里面没有和out有关的,但是这个out已经在之前被确定好放在哪里了
        // 但是这个寄存器实际上是一个现有的能分配的,这样就会导致被out拦截
        // 求出每一个值的最后使用
        for (Instr instr : block.instrList) {
            for (Value value : instr.paras) {
                if ((value instanceof LocalVar || value instanceof Param)) {
                    lastUse.put(value, instr);
                }
            }
        }
        // 这里将lastUse解放是有些不合理的,假如我原本有了一个值,已经分配寄存器了
        // 然后这里这个值和那个公用一个寄存器,然后这里由于是lastUse,所以直接放了
        // 但是考虑到已经分配了,也不会给原来的那个再次分配寄存器,就导致这个寄存器直接丢了
        // 解决:如果已经分配了寄存器了,那么就让regUsed把这个寄存器占了
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
            if (instr.getAns() != null) {
                if (!manager.varSReg.containsKey(instr.getAns())) {
                    if (manager.hasUsableReg() && !manager.notHasReg.contains(instr.getAns())) {
                        manager.allocOnReg(instr.getAns());
                    } else {
                        manager.notHasReg.add(instr.getAns());
                    }
                } else {
                    manager.refillAllocReg(instr.getAns());
                }
            }
        }
        HashSet<Register> store = new HashSet<>(manager.regUsed);
        // 这里遍历的应该是后驱吧,而不是直接支配的,应该是next
        // 活跃变量分析和支配没关系的
        for (BasicBlock child : block.next) {
            if (!visited.contains(child)) {
                blockAlloc(child, visited);
                // 一定要在下一次遍历前把东西都恢复了
                manager.regUsed.clear();
                manager.regUsed.addAll(store);
            }
        }
        // 考虑到这里定义的寄存器不再需要了(因为它支配的块没了,剩下的块由于不确定是否经过这个块,因此必然不会用到,因此我们可以把它定义的删掉)
        // 事实上,这是一个极其错误的决定
        // 例如 b18->b6,b5->b6,这两个人都定义了变量54,b6中要用到54,但是这都不算在b18或者b5的直接支配节点里面
        // 导致出来之后54分配的就被放了
        // 所以不是支配,而是后继
        // 而且,不能把所有的都放了,例如 b5处对该变量定义为23,进入b6也为23，
        // 分别进入b9,b11,都是定义22,然后统一跳到b7再进行赋值给23回到b5
        // 但是考虑到中间跳到b7的时候也会出现def了23,于是会导致在不该的地方释放23的寄存器
        for (Value value : block.def) {
            if (manager.varSReg.containsKey(value)) {
                System.out.print("def " + value + " :");
                manager.releaseReg(manager.varSReg.get(value));
            }
        }
        // 恢复所有所谓的死变量
        for (Value value : dead) {
            if (manager.varSReg.containsKey(value) && !block.def.contains(value)) {
                manager.regUsed.add(manager.varSReg.get(value));
            }
        }
        System.out.println("-------------" + block.name + "------------end");
    }
}
