package front_end.AST.Exp;

import front_end.AST.Node;
import front_end.AST.TokenNode;
import mid_end.llvm_ir.IRBuilder;
import mid_end.llvm_ir.Instrs.IcmpIr;
import mid_end.llvm_ir.Value;
import mid_end.llvm_ir.type.BaseType;

import java.util.ArrayList;

public class RelExp extends Node {
    private final AddExp addExp;
    private final ArrayList<TokenNode> tokenNodes;
    private final ArrayList<AddExp> otherAddExps;

    public RelExp(AddExp addExp) {
        this.addExp = addExp;
        this.otherAddExps = new ArrayList<>();
        this.tokenNodes = new ArrayList<>();
    }

    public void addAddExp(TokenNode tokenNode, AddExp AddExp) {
        tokenNodes.add(tokenNode);
        otherAddExps.add(AddExp);
    }

    @Override
    public void show() {
        addExp.show();
        System.out.println("<RelExp>");
        for (int i = 0; i < otherAddExps.size(); i++) {
            tokenNodes.get(i).show();
            otherAddExps.get(i).show();
            System.out.println("<RelExp>");
        }

        super.show();
    }

    @Override
    public Value getIRCode() {
        Value addValue = addExp.getIRCode();
        // 一般返回的是一个i32
        for (int i = 0; i < otherAddExps.size(); i++) {
            Value I32Value = BaseType.ensureReturnType(BaseType.I32, addValue);
            String opcode = IcmpIr.chooseString(tokenNodes.get(i));
            // 要保证 除了本身是要I32之外，添加上的也是I32
            Value BeI32Value = BaseType.ensureReturnType(BaseType.I32, otherAddExps.get(i).getIRCode());
            IcmpIr icmpIr = new IcmpIr(opcode, I32Value, BeI32Value);
            IRBuilder.IB.addInstrForBlock(icmpIr);
            addValue = icmpIr.getAns();
        }
        // 可以看出，返回的有可能是32(直接把add的值返回回来了)，也有可能是i1，把icmp的值返回回来了。
        return addValue;
    }
}
