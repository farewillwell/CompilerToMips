package front_end.AST.Exp;

import front_end.AST.Node;
import mid_end.llvm_ir.BasicBlock;

public class Cond extends Node {

    private final LOrExp lOrExp;

    public Cond(LOrExp lOrExp) {
        this.lOrExp = lOrExp;
    }

    @Override
    public void show() {
        lOrExp.show();
        System.out.println("<Cond>");
        super.show();
    }

    public void genCondIr(BasicBlock thenBlock, BasicBlock elseBlock) {
        lOrExp.genLOrIr(thenBlock, elseBlock);
    }
}
