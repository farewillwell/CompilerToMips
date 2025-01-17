package optimization;

import mid_end.llvm_ir.*;
import mid_end.llvm_ir.Instrs.ALUIr;
import mid_end.llvm_ir.Instrs.IcmpIr;
import mid_end.llvm_ir.Instrs.ZextIr;

import java.util.HashMap;
import java.util.Iterator;

public class GVN {
    public void solve(IRModule irModule) {
        for (Function function : irModule.functions) {
            constFolding(function);
            doGVN(function);
            jumpChange(function);
        }
    }

    private void constFolding(Function function) {
        // 策略,在pc2move之前使用考虑到llvm value是单次赋值的,所以一个表达式得到的结果必然没有在之前被用过
        // 之后用到的也就是当前这个值,而不会给里面赋值什么的,所以可以直接替换
        for (BasicBlock block : function.basicBlocks) {
            Iterator<Instr> iterator = block.instrList.iterator();
            while (iterator.hasNext()) {
                Instr instr = iterator.next();
                if (instr instanceof ALUIr && ((ALUIr) instr).foldConst()) {
                    instr.isDeleted = true;
                    iterator.remove();
                } else if (instr instanceof IcmpIr && ((IcmpIr) instr).foldConst()) {
                    instr.isDeleted = true;
                    iterator.remove();
                } else if (instr instanceof ZextIr && ((ZextIr) instr).foldSelf()) {
                    instr.isDeleted = true;
                    iterator.remove();
                }
            }
        }
    }

    private void doGVN(Function function) {
        HashMap<String, Value> gvnMap = new HashMap<>();
        GVNRemove(function.basicBlocks.get(0), gvnMap);
    }

    private void GVNRemove(BasicBlock block, HashMap<String, Value> gvnMap) {
        HashMap<String, Value> stack = new HashMap<>(gvnMap);
        Iterator<Instr> iterator = block.instrList.iterator();
        while (iterator.hasNext()) {
            Instr instr = iterator.next();
            if (instr.canBeGVN()) {
                String hash = instr.gvnCode();
                if (gvnMap.containsKey(hash)) {
                    if (instr.getAns() != null) {
                        instr.getAns().userReplaceMeWith(gvnMap.get(hash));
                        iterator.remove();
                    }
                } else {
                    if (instr.getAns() != null) {
                        gvnMap.put(hash, instr.getAns());
                    } else {
                        iterator.remove();
                    }

                }
            }
        }
        for (BasicBlock basicBlock : block.beImmDom) {
            GVNRemove(basicBlock, gvnMap);
        }
        gvnMap.clear();
        gvnMap.putAll(stack);
    }

    private void jumpChange(Function function) {
        for (BasicBlock block : function.basicBlocks) {
            block.constBranchTpJump();
            // 仅有这里会影响jump的范围,会影响支配和前后驱
            // TODO 仅仅修改了前后驱,支配关系还未修改!!!但是支配关系的话,已经完成消phi了，应当不再需要用这个了
        }
    }
}
