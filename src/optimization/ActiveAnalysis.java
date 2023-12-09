package optimization;

import mid_end.llvm_ir.*;

import java.util.ArrayList;
import java.util.HashSet;

public class ActiveAnalysis {
    // 活跃变量分析,注意,此时我们已经完成了消phi指令，目前只有move,没有phi和pcopy
    // 已经建好了前驱和后继的图,但是实际上由于常量折叠，我们对前驱和后继进行了一定的修正
    // 但是dom似乎还没有修正
    // 如果有必要的话，需要对func进行一下清空,这个在cfg中实现
    public void solve(IRModule irModule) {
        irModule.reQueryDom();
        System.out.println("------------active---------------");
        for (Function function : irModule.functions) {
            System.out.println("----------" + function.name + "---------------");
            for (BasicBlock block : function.basicBlocks) {
                blockDefUse(block);
            }
            queryInOut(function);
        }
    }

    // use的部分不存在于
    private void blockDefUse(BasicBlock block) {
        System.out.println("-----------" + block.name + "----------------");
        for (Instr instr : block.instrList) {
            // use的判断先于def
            if (instr.getAns() != null && !block.use.contains(instr.getAns())) {
                block.def.add(instr.getAns());
            }
            for (Value value : instr.paras) {
                // 有且仅有这个才有价值,constant什么的没有价值
                if ((value instanceof LocalVar || value instanceof Param) && !block.def.contains(value)) {
                    block.use.add(value);
                }
            }
        }
        System.out.print("use: ");
        for (Value value : block.use) {
            System.out.print(value + " ");
        }
        System.out.print("\ndef: ");
        for (Value value : block.def) {
            System.out.print(value + " ");
        }
        System.out.println();
    }

    private void queryInOut(Function function) {
        // 如何知道这个东西的EXIT块?或者说如果EXIT块有很多怎么办?
        // 没有必要,因为这个本质上是因为倒序速度比较快,我们也倒
        /* IN[EXIT] ={} 由于我们hashset初始都是空的，所以满足
         *  for(除EXIT之外的每个基本块B)IN[B] = {} 还是满足,初始IN都是空嘛*/
        ArrayList<BasicBlock> blocks = function.basicBlocks;
        boolean inChanged = true;//用来记录某个IN值是否发生了改变的变量
        // 如何判断是不是exit? 因为可能有多个return: exit <==> size(next)=0,无后继节点,OUT恒为空
        // while(某个IN的值发生了改变)
        while (inChanged) {
            inChanged = false;
            for (int i = blocks.size() - 1; i >= 0; i--) {
                BasicBlock b = blocks.get(i);
                //OUT[B]=Us是B的一个后继IN[s]
                //这个是等于,而不是add,但是事实上只会加.不过不差这点性能
                b.out.clear();
                for (BasicBlock s : b.next) {
                    b.out.addAll(s.in);
                }
                // IN[B] = useB U (OUT[B] - defB)
                // 可以变换操作先取out,再减def,最后加上user
                HashSet<Value> newIn = new HashSet<>(b.out);
                newIn.removeAll(b.def);
                newIn.addAll(b.use);
                // 该IN值发生了改变
                if (newIn.size() != b.in.size() || !newIn.containsAll(b.in)) {
                    b.in.clear();
                    b.in.addAll(newIn);
                    inChanged = true;
                }

            }
        }
        for (BasicBlock b : blocks) {
            System.out.println(b.name + " : ");
            System.out.print("in : ");
            for (Value value : b.in) {
                System.out.print(value + " ");
            }
            System.out.print("\nout : ");
            for (Value value : b.out) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }

}
