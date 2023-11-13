package front_end.AST.Exp;

import front_end.AST.Node;
import front_end.AST.TokenNode;
import front_end.ErrorCollector;
import front_end.RW;
import mid_end.llvm_ir.IRBuilder;
import mid_end.llvm_ir.Instrs.ALUInstr;
import mid_end.llvm_ir.Value;

import java.util.ArrayList;

public class MulExp extends Node {
    private final UnaryExp unaryExp;
    private final ArrayList<UnaryExp> otherUnaryExps;
    private final ArrayList<TokenNode> tokenNodes;

    public MulExp(UnaryExp unaryExp) {
        this.unaryExp = unaryExp;
        this.otherUnaryExps = new ArrayList<>();
        tokenNodes = new ArrayList<>();
    }

    public void addUnaryExp(TokenNode tokenNode, UnaryExp unaryExp) {
        tokenNodes.add(tokenNode);
        otherUnaryExps.add(unaryExp);
    }

    @Override
    public void show() {
        unaryExp.show();
        System.out.println("<MulExp>");
        for (int i = 0; i < otherUnaryExps.size(); i++) {
            tokenNodes.get(i).show();
            otherUnaryExps.get(i).show();
            System.out.println("<MulExp>");
        }

        super.show();
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        unaryExp.checkError(errorCollector);
        for (UnaryExp unaryExp1 : otherUnaryExps) {
            unaryExp1.checkError(errorCollector);
        }
    }

    public int evaluate() {
        int temp = unaryExp.evaluate();
        for (int i = 0; i < tokenNodes.size(); i++) {
            if (tokenNodes.get(i).type() == RW.TYPE.MULT) {
                temp *= otherUnaryExps.get(i).evaluate();
            } else if (tokenNodes.get(i).type() == RW.TYPE.DIV) {
                temp /= otherUnaryExps.get(i).evaluate();
            } else {
                temp %= otherUnaryExps.get(i).evaluate();
            }
        }
        return temp;
    }

    public int getDim() {
        int dim = unaryExp.getDim();
        for (UnaryExp unaryExp1 : otherUnaryExps) {
            dim = Integer.max(dim, unaryExp1.getDim());
        }
        return dim;
    }

    public int getSec() {
        int sec = unaryExp.getSec();
        for (UnaryExp unaryExp1 : otherUnaryExps) {
            sec = Integer.min(sec, unaryExp1.getSec());
        }
        return sec;
    }

    @Override
    public Value getIRCode() {
        Value value = unaryExp.getIRCode();
        for (int i = 0; i < otherUnaryExps.size(); i++) {
            String opcode;
            RW.TYPE type = tokenNodes.get(i).type();
            if (type == RW.TYPE.MULT) {
                opcode = ALUInstr.MUL;
            } else if (type == RW.TYPE.DIV) {
                opcode = ALUInstr.DIV;
            } else {
                opcode = ALUInstr.SREM;
            }
            ALUInstr aluInstr = new ALUInstr(opcode, value, otherUnaryExps.get(i).getIRCode());
            IRBuilder.IB.addInstrForBlock(aluInstr);
            value = aluInstr.getAns();
        }
        return value;
    }

    @Override
    public int queryValue() {
        int ans = unaryExp.queryValue();
        for (int i = 0; i < otherUnaryExps.size(); i++) {
            if (tokenNodes.get(i).type() == RW.TYPE.MULT) {
                ans *= otherUnaryExps.get(i).queryValue();
            } else if (tokenNodes.get(i).type() == RW.TYPE.DIV) {
                ans /= otherUnaryExps.get(i).queryValue();
            } else {
                ans %= otherUnaryExps.get(i).queryValue();
            }
        }
        return ans;
    }
}
