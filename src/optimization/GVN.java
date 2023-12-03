package optimization;

import mid_end.llvm_ir.BasicBlock;
import mid_end.llvm_ir.Function;
import mid_end.llvm_ir.IRModule;
import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.Instrs.ALUInstr;
import mid_end.llvm_ir.Instrs.IcmpInstr;
import mid_end.llvm_ir.Instrs.ZextInstr;

import java.util.Iterator;

public class GVN {
    public void solve(IRModule irModule) {
        for (Function function : irModule.functions) {
            constFolding(function);
            jumpChange(function);
        }
    }

    private void constFolding(Function function) {
        // 策略,考虑到llvm value是单次赋值的,所以一个表达式得到的结果必然没有在之前被用过
        // 之后用到的也就是当前这个值,而不会给里面赋值什么的,所以可以直接替换
        for (BasicBlock block : function.basicBlocks) {
            Iterator<Instr> iterator = block.instrList.iterator();
            while (iterator.hasNext()) {
                Instr instr = iterator.next();
                if (instr instanceof ALUInstr && ((ALUInstr) instr).foldConst()) {
                    iterator.remove();
                } else if (instr instanceof IcmpInstr && ((IcmpInstr) instr).foldConst()) {
                    iterator.remove();
                } else if (instr instanceof ZextInstr && ((ZextInstr) instr).foldSelf()) {
                    iterator.remove();
                }
            }
        }
    }

    private void jumpChange(Function function) {
        for (BasicBlock block : function.basicBlocks) {
            block.constBranchTpJump();
            // 仅有这里会影响jump的范围,会影响支配和前后驱
            // TODO 仅仅修改了前后驱,支配关系还未修改!!!但是支配关系的话,已经完成消phi了，应当不再需要用这个了
        }
    }
}
