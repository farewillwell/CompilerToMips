package front_end.AST.Exp;

import front_end.AST.Node;
import mid_end.llvm_ir.BasicBlock;
import mid_end.llvm_ir.IRBuilder;

import java.util.ArrayList;

public class LOrExp extends Node {
    private final ArrayList<LAndExp> lAndExps;

    public LOrExp(LAndExp exp) {
        this.lAndExps = new ArrayList<>();
        this.lAndExps.add(exp);
    }

    public void addLAndExp(LAndExp exp) {
        lAndExps.add(exp);
    }

    public void show() {
        for (LAndExp exp : lAndExps) {
            if (lAndExps.indexOf(exp) != lAndExps.size() - 1) {
                System.out.println("OR ||");
            }
            exp.show();
            System.out.println("<LOrExp>");
        }
        super.show();
    }

    public void genLOrIr(BasicBlock thenBlock, BasicBlock elseBlock) {
        // thenBlock 是公用的，只要有一个生效都会向那里跳，else block只有都不生效了才会向里面跳，那就是最后的事情了
        for (int i = 0; i < lAndExps.size(); i++) {
            if (i == lAndExps.size() - 1) {
                lAndExps.get(i).getLAndIr(thenBlock, elseBlock);
            } else {
                BasicBlock nextBlock = new BasicBlock();
                lAndExps.get(i).getLAndIr(thenBlock, nextBlock);
                IRBuilder.IB.enterBlock(nextBlock);
            }
        }
    }
}
