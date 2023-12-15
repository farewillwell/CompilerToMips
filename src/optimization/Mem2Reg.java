package optimization;

import mid_end.llvm_ir.*;
import mid_end.llvm_ir.Instrs.AllocIr;
import mid_end.llvm_ir.Instrs.LoadIr;
import mid_end.llvm_ir.Instrs.PhiIr;
import mid_end.llvm_ir.Instrs.StoreIr;
import mid_end.llvm_ir.type.ArrayType;
import mid_end.llvm_ir.type.PointerType;

import java.util.*;

public class Mem2Reg {
    // 程序中所有变量?需要涉及到其他函数吗?
    // 应当是按照函数来的
    private final ArrayList<Value> variableNames = new ArrayList<>();

    private final HashMap<Value, HashSet<BasicBlock>> defs = new HashMap<>();

    private final HashMap<Value, Stack<Value>> reachDefs = new HashMap<>();

    public void solve(IRModule IRModule) {
        for (Function function : IRModule.functions) {
            IRBuilder.IB.enterFunc(function);
            System.out.println("-----------------Mem2Reg---------" + function.name + "-------------------------");
            variableNames.clear();
            defs.clear();
            reachDefs.clear();
            searchVariableNames(function);
            for (Value value : variableNames) {
                solveVarPhi(value);
            }
            BasicBlock entry = function.basicBlocks.get(0);
            rename(entry);
        }
    }

    private void searchVariableNames(Function func) {
        for (BasicBlock block : func.basicBlocks) {
            for (Instr instr : block.instrList) {
                if (instr instanceof AllocIr && !(((PointerType) instr.getAns().type).objType instanceof ArrayType)) {
                    variableNames.add(instr.getAns());
                    defs.put(instr.getAns(), new HashSet<>());
                    // 其中，alloc必然是该变量第一次亮相，所以我只需要判断已经加上的variableNames即可.
                } else if (instr instanceof StoreIr) {
                    for (Value var : variableNames) {
                        if (((StoreIr) instr).hasDef(var)) {
                            defs.get(var).add(block);
                        }
                    }
                }
            }
        }
    }

    // 对一个变量解决插phi问题
    private void solveVarPhi(Value var) {
        HashSet<BasicBlock> F = new HashSet<>();
        // F <- {}
        LinkedList<BasicBlock> W = new LinkedList<BasicBlock>(defs.get(var)) {
        };
        //W <- {}
        // W <- W + B (contains def of var)
        while (W.size() != 0) {
            BasicBlock X = W.removeFirst();
            // remove a block X from W
            for (BasicBlock Y : X.df) {
                if (!F.contains(Y)) {
                    F.add(Y);
                    // insert phi
                    Y.instrList.add(0, new PhiIr(var));
                    if (!defs.get(var).contains(Y)) {
                        W.add(Y);
                    }
                }
            }
        }
    }
    // TODO 做这个前要先删除所有到不了的基本块，不然会导致llvm将已经删除的指针啥的错误的保留
    // 因为rename调用的是对所有dom能到达的进行的

    // 注意，rename是不管全局变量和数组的，所以整之前要检查是不是用alloc开栈了。
    // 同时 alloc 开栈也要注意要开的一定是一个局部非数组变量
    // 如何保证我所有的栈起到了功效？因为如果退回，那么就不直接支配了，那在这里面定义的东西都无效了
    // 可以在开始的时候存一个hashMap，代表每一个变量的栈深度，然后每次退回的时候归0
    // 谨记:你不会知道指令流到底是啥样,但是栈上没有的就不管
    private void rename(BasicBlock block) {
        ArrayList<Instr> instrList = block.instrList;
        Iterator<Instr> iterator = instrList.iterator();
        //-----------------------------------------------------------
        // 保存栈状态
        HashMap<Value, Integer> stackBefore = new HashMap<>();
        for (Value value : reachDefs.keySet()) {
            stackBefore.put(value, reachDefs.get(value).size());
        }
        //---------------------------------------------------------
        while (iterator.hasNext()) {
            Instr instr = iterator.next();
            if (instr instanceof AllocIr) {
                // 数组不搞rename
                if (instr.type instanceof ArrayType) {
                    continue;
                }
                reachDefs.put(instr.getAns(), new Stack<>());
                instr.isDeleted=true;
                iterator.remove();
            } else if (instr instanceof StoreIr) {
                if (!reachDefs.containsKey(((StoreIr) instr).getDstPointer())) {
                    continue;
                }
                reachDefs.get(((StoreIr) instr).getDstPointer()).push(((StoreIr) instr).getStoreInValue());
                iterator.remove();
                instr.isDeleted=true;
            } else if (instr instanceof PhiIr) {
                if (!reachDefs.containsKey(((PhiIr) instr).tieValue)) {
                    continue;
                }
                reachDefs.get(((PhiIr) instr).tieValue).push(instr.getAns());
                // WARNING 这里不能删除!!!!
            } else if (instr instanceof LoadIr) {
                if (!reachDefs.containsKey(((LoadIr) instr).getFromPointer())) {
                    continue;
                }
                // 定义了倒是，但是一定有值吗？
                // 不一定,那怎么办?这个时候是未定义的?
                // 未定义的统统赋值为0，如果因为这个错了的话那就见鬼了.
                if (reachDefs.get(((LoadIr) instr).getFromPointer()).empty()) {
                    instr.getAns().userReplaceMeWith(new Constant());
                } else {
                    Value newValue = reachDefs.get(((LoadIr) instr).getFromPointer()).peek();
                    instr.getAns().userReplaceMeWith(newValue);
                }
                instr.isDeleted=true;
                iterator.remove();
            }
        }
        //------------------------- refill phi-----------------------------------//
        for (BasicBlock next : block.next) {
            for (Instr instr : next.instrList) {
                if (instr instanceof PhiIr) {
                    // 为什么会遇到前两种这样的情况呢?显然因为一开始的块携带的东西太少了，但是也被算做前驱了
                    if (!reachDefs.containsKey(((PhiIr) instr).tieValue)) {
                        // 如果是不存在这个,代表未定义，这里暂时赋值成0,constant 默认是undefine
                        ((PhiIr) instr).refill(new Constant(), block);
                        continue;
                    }
                    if (reachDefs.get(((PhiIr) instr).tieValue).empty()) {
                        // 如果是empty,代表未定义，这里暂时赋值成0
                        ((PhiIr) instr).refill(new Constant(), block);
                        continue;
                    }
                    Value newValue = reachDefs.get(((PhiIr) instr).tieValue).peek();
                    ((PhiIr) instr).refill(newValue, block);
                }
            }
        }
        //-----------------------continue dfs-----------------------------------//
        for (BasicBlock domNext : block.beImmDom) {
            rename(domNext);
        }
        //---------------------------回退栈状态------------------------------//
        reachDefs.keySet().retainAll(stackBefore.keySet());
        for (Value value : reachDefs.keySet()) {
            int size = stackBefore.get(value);
            while (reachDefs.get(value).size() > size) {
                reachDefs.get(value).pop();
            }
        }
    }
}
