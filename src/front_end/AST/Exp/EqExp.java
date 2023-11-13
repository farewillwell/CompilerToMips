package front_end.AST.Exp;

import front_end.AST.Node;
import front_end.AST.TokenNode;
import mid_end.llvm_ir.IRBuilder;
import mid_end.llvm_ir.Instrs.IcmpInstr;
import mid_end.llvm_ir.Value;
import mid_end.llvm_ir.type.BaseType;

import java.util.ArrayList;

public class EqExp extends Node {
    private final RelExp relExp;
    private final ArrayList<TokenNode> tokenNodes;
    private final ArrayList<RelExp> otherRelExps;

    public EqExp(RelExp relExp) {
        this.relExp = relExp;
        this.otherRelExps = new ArrayList<>();
        this.tokenNodes = new ArrayList<>();
    }

    public void addRelExp(TokenNode tokenNode, RelExp relExp) {
        tokenNodes.add(tokenNode);
        otherRelExps.add(relExp);
    }

    @Override
    public void show() {
        relExp.show();
        System.out.println("<EqExp>");
        for (int i = 0; i < otherRelExps.size(); i++) {
            tokenNodes.get(i).show();
            otherRelExps.get(i).show();
            System.out.println("<EqExp>");
        }

        super.show();
    }

    @Override
    public Value getIRCode() {
        Value eqValue = relExp.getIRCode(); //得到的一般是1,而返回的也需要是1，
        // 但是不确定，最好check一下
        for (int i = 0; i < otherRelExps.size(); i++) {
            Value I32Value = BaseType.ensureReturnType(BaseType.I32, eqValue);
            String opcode = IcmpInstr.chooseString(tokenNodes.get(i));
            IcmpInstr icmpInstr = new IcmpInstr(opcode, I32Value, otherRelExps.get(i).getIRCode());
            IRBuilder.IB.addInstrForBlock(icmpInstr);
            eqValue = icmpInstr.getAns();
        }
        return BaseType.ensureReturnType(BaseType.I1, eqValue);
    }
}
