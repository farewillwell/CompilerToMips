package optimization;

import back_end.Mips.Register;
import back_end.Mips.VarManager;
import mid_end.llvm_ir.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class RegAlloc {
    private VarManager manager = null;

    public void solve(IRModule irModule) {
        for (Function function : irModule.functions) {
            manager = function.varManager;
            alloc(function);
            otherAlloc(function);
        }
    }

    private void alloc(Function function) {
        ArrayList<Param> params = function.funcParams;
        for (int i = 0; i < params.size() && manager.hasUseAbleReg(); i++) {
            manager.allocOnReg(params.get(i));
        }
        //这里应该仅仅对入口使用,否则会在支配树的dfs中额外多出很多操作
        System.out.println("-------------alloc for " + function.name + " ---------------");
        blockAlloc(function.basicBlocks.get(0));
    }

    /*前排提示,该方法不是我个人想出来的,而是参考hyggge的,之前我用的是前后序遍历,但是并不能处理in,out寄存器都分配情况下的冲突*/
    // 太玄学了,改成支配的立马就过了,前后驱的活跃变量不行啊....
    // 因为支配是前后驱的上位
    // 按这个遍历不会存在顺序问题
    // 同时,由于phi什么的都是按照支配设置的,这个方法能很有效的消除
    private void blockAlloc(BasicBlock block) {
        ArrayList<Instr> instrList = block.instrList;
        HashSet<Value> dead = new HashSet<>();
        HashMap<Value, Value> lastUse = new HashMap<>();
        // 遍历value,找到其最后一次使用
        for (Instr instr : instrList) {
            for (Value value : instr.paras) {
                if ((value instanceof LocalVar || value instanceof Param)) {
                    lastUse.put(value, instr);
                }
            }
        }
        // 如果不在out里面,那么自然就可以放掉了,因为根本出不去
        for (Instr instr : instrList) {
            for (Value value : instr.paras) {
                if (lastUse.get(value) == instr && !block.out.contains(value) && manager.varSReg.containsKey(value)) {
                    manager.regUsedValue.remove(manager.varSReg.get(value));
                    dead.add(value);
                }
            }
            if (instr.getAns() != null) {
                System.out.println(instr.getAns() + "----");
                if (manager.hasUseAbleReg()) {
                    manager.allocOnReg(instr.getAns());
                }
            }
        }
        // 遍历所有支配子节点
        for (BasicBlock basicBlock : block.beImmDom) {
            HashMap<Register, Value> stack = new HashMap<>(manager.regUsedValue);
            for (Register register : stack.keySet()) {
                Value value = manager.regUsedValue.get(register);
                if (!basicBlock.in.contains(value)) {
                    manager.regUsedValue.remove(register);
                }
            }
            // 恢复栈状态,保证dfs的不变性
            blockAlloc(basicBlock);
            manager.regUsedValue.clear();
            manager.regUsedValue.putAll(stack);
        }
        // 这里由于是走完了支配树，所以它定义的都不会再被用到了,所以可以释放def
        // 你说的对,但是这就是支配特性带给我的自信
        // 当然,我们要保留这个value的寄存器分配结果
        for (Value value : block.def) {
            if (manager.varSReg.containsKey(value)) {
                manager.regUsedValue.remove(manager.varSReg.get(value));
            }
        }
        //在这里认为是死的,但是在别的树节点可不认为是死的
        for (Value value : dead) {
            if (manager.varSReg.containsKey(value) && !block.def.contains(value)) {
                manager.regUsedValue.put(manager.varSReg.get(value), value);
            }
        }
    }

    private void otherAlloc(Function function) {
        LinkedList<Register> useAble = new LinkedList<>(manager.usableRegs);
        useAble.removeAll(manager.varSReg.values());
        for (BasicBlock block : function.basicBlocks) {
            for (Instr instr : block.instrList) {
                for (Value value : instr.paras) {
                    if (useAble.size() != 0 && !manager.varSReg.containsKey(value)&&
                            (value instanceof LocalVar || value instanceof Param)) {
                        Register register = useAble.removeFirst();
                        manager.varSReg.put(value, register);
                        System.out.println(register+"----"+value);
                    }
                }
            }
        }
    }
}
