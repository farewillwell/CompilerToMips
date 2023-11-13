package front_end.AST.Exp;

import front_end.AST.Node;
import mid_end.llvm_ir.BasicBlock;
import mid_end.llvm_ir.IRBuilder;
import mid_end.llvm_ir.Instrs.BranchInstr;

import java.util.ArrayList;

public class LAndExp extends Node {
    private final ArrayList<EqExp> eqExps;

    public LAndExp(EqExp exp) {
        this.eqExps = new ArrayList<>();
        eqExps.add(exp);
    }

    public void addEqExp(EqExp exp) {
        eqExps.add(exp);
    }

    @Override
    public void show() {
        for (EqExp exp : eqExps) {
            if (eqExps.indexOf(exp) != eqExps.size() - 1) {
                System.out.println("AND &&");
            }
            exp.show();
            System.out.println("<LAndExp>");
        }
        super.show();
    }

    public void getLAndIr(BasicBlock thenBlock, BasicBlock elseBlock) {
        // 只要有一个不生效，跳往的都是else block，这个和or是有区别的
        for (int i = 0; i < eqExps.size(); i++) {
            if (i == eqExps.size() - 1) {
                BranchInstr branchInstr = new BranchInstr(eqExps.get(i).getIRCode(), thenBlock, elseBlock);
                IRBuilder.IB.addInstrForBlock(branchInstr);
            } else {
                BasicBlock nextBlock = new BasicBlock();
                BranchInstr branchInstr = new BranchInstr(eqExps.get(i).getIRCode(), nextBlock, elseBlock);
                IRBuilder.IB.addInstrForBlock(branchInstr);
                IRBuilder.IB.enterBlock(nextBlock);
            }
        }
    }
}
