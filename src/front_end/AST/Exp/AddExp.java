package front_end.AST.Exp;

import front_end.AST.Node;
import front_end.AST.TokenNode;
import front_end.ErrorCollector;
import front_end.RW;
import mid_end.llvm_ir.IRBuilder;
import mid_end.llvm_ir.Instrs.ALUInstr;
import mid_end.llvm_ir.Value;

import java.util.ArrayList;

public class AddExp extends Node {
    private final MulExp mulExp;
    private final ArrayList<TokenNode> tokenNodes;
    private final ArrayList<MulExp> otherMulExps;

    public AddExp(MulExp mulExp) {
        this.mulExp = mulExp;
        this.otherMulExps = new ArrayList<>();
        this.tokenNodes = new ArrayList<>();
    }

    public void addMulExp(TokenNode tokenNode, MulExp mulExp) {
        tokenNodes.add(tokenNode);
        otherMulExps.add(mulExp);
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        mulExp.checkError(errorCollector);
        for (MulExp mulExp1 : otherMulExps) {
            mulExp1.checkError(errorCollector);
        }
    }

    @Override
    public void show() {
        mulExp.show();
        System.out.println("<AddExp>");
        for (int i = 0; i < otherMulExps.size(); i++) {
            tokenNodes.get(i).show();
            otherMulExps.get(i).show();
            System.out.println("<AddExp>");
        }
        super.show();
    }

    public int evaluate() {
        int temp = mulExp.evaluate();
        for (int i = 0; i < tokenNodes.size(); i++) {
            if (tokenNodes.get(i).type() == RW.TYPE.PLUS) {
                temp += otherMulExps.get(i).evaluate();
            } else {
                temp -= otherMulExps.get(i).evaluate();
            }
        }
        return temp;
    }

    public int getDim() {
        int dim = mulExp.getDim();
        for (MulExp mulExp1 : otherMulExps) {
            dim = Integer.max(dim, mulExp1.getDim());
        }
        return dim;
    }

    public int getSec() {
        int sec = mulExp.getSec();
        for (MulExp mulExp1 : otherMulExps) {
            sec = Integer.min(sec, mulExp1.getSec());
        }
        return sec;
    }


    @Override
    public Value getIRCode() {
        Value value = mulExp.getIRCode();
        for (int i = 0; i < otherMulExps.size(); i++) {
            String opcode = tokenNodes.get(i).type() == RW.TYPE.PLUS ? ALUInstr.ADD : ALUInstr.SUB;
            ALUInstr aluInstr = new ALUInstr(opcode, value, otherMulExps.get(i).getIRCode());
            IRBuilder.IB.addInstrForBlock(aluInstr);
            value = aluInstr.getAns();
        }
        return value;
    }

    @Override
    public int queryValue() {
        int ans = mulExp.queryValue();
        for (int i = 0; i < otherMulExps.size(); i++) {
            ans += (tokenNodes.get(i).type() == RW.TYPE.PLUS ? 1 : -1) * otherMulExps.get(i).queryValue();
        }
        return ans;
    }
}
